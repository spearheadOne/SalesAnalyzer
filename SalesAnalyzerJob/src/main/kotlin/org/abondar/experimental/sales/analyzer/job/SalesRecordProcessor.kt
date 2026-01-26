package org.abondar.experimental.sales.analyzer.job

import com.google.protobuf.Timestamp
import io.micronaut.serde.ObjectMapper
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.abondar.experimental.sales.analyzer.data.AggDto
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.data.AggRow.Companion.toDto
import org.abondar.experimental.sales.analyzer.data.OrigPrice
import org.abondar.experimental.sales.analyzer.data.SalesRecord
import org.abondar.experimental.sales.analyzer.fx.ConvertBatchRequest
import org.abondar.experimental.sales.analyzer.fx.ConvertRequestItem
import org.abondar.experimental.sales.analyzer.fx.Money
import org.abondar.experimental.sales.analyzer.job.fx.FxClient
import org.abondar.experimental.sales.analyzer.job.mapper.AggMapper
import org.abondar.experimental.sales.analyzer.job.queue.SqsProducer
import org.slf4j.LoggerFactory
import software.amazon.kinesis.lifecycle.events.*
import software.amazon.kinesis.processor.ShardRecordProcessor
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

data class PendingRow(
    val row: AggRow.Builder,
    val originalPrice: OrigPrice
)

class SalesRecordProcessor(
    private val objectMapper: ObjectMapper,
    private val aggMapper: AggMapper,
    private val sqsProducer: SqsProducer,
    private val fxClient: FxClient,
    private val defaultCurrency: String,
    private val processTimeout: Long
) : ShardRecordProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun initialize(initInput: InitializationInput?) {
        log.info("Initializing record processor with shard ID: ${initInput?.shardId()}")
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput?) {
        val defCur = Currency.getInstance(defaultCurrency)
        val aggRows = mutableListOf<AggRow>()
        val fxRows = mutableMapOf<String, PendingRow>()
        val convertRequestBatch = mutableListOf<ConvertRequestItem>()
        val messages = mutableListOf<AggDto>()

        processRecordsInput?.records()?.forEach { rec ->
            try {
                val recBytes = rec.data().asReadOnlyBuffer().let { buffer ->
                    ByteArray(buffer.remaining()).apply { buffer.get(this) }
                }

                val salesRecord = objectMapper.readValue(recBytes, SalesRecord::class.java)
                val recordCurrency = Currency.getInstance(salesRecord.currency)

                val row = AggRow.builder().apply {
                    eventTime = salesRecord.timestamp
                    productName = salesRecord.productName
                    productId = salesRecord.productId
                    units = salesRecord.amount
                    category = salesRecord.category
                    this.currency = defCur.currencyCode
                }

                if ( recordCurrency == defCur) {
                    row.revenue = salesRecord.price.multiply(BigDecimal.valueOf(salesRecord.amount))
                    val aggRow = row.build()
                    aggRows.add(aggRow)
                    messages.add(aggRow.toDto())
                } else {
                    val correlationId = UUID.randomUUID().toString()
                    convertRequestBatch.add(
                        buildConvertRequestItem(
                            salesRecord.price, recordCurrency,
                            salesRecord.timestamp, defCur,
                            correlationId
                        )
                    )
                    fxRows[correlationId] = PendingRow(row, OrigPrice(salesRecord.price.toPlainString(),
                        recordCurrency.currencyCode))
                }

            } catch (ex: Exception) {
                log.error("Error processing record", ex)
            }
        }

        if (convertRequestBatch.isNotEmpty()) {
            val convertBatchResponse = runBlocking {
                withTimeout(processTimeout.milliseconds) {
                    fxClient.convertBatch(
                        ConvertBatchRequest.newBuilder()
                            .addAllItems(convertRequestBatch)
                            .build()
                    )
                }
            }

            convertBatchResponse.itemsList.forEach { res ->
                val pending = fxRows.remove(res.correlationId)
                    ?: error("Unknown correlationId: ${res.correlationId}")
                val r = pending.row

                val convertedPrice = res.converted.amount.toBigDecimal()
                r.revenue = convertedPrice.multiply(BigDecimal(r.units))
                    .setScale(defCur.defaultFractionDigits.coerceAtLeast(0), RoundingMode.HALF_UP)


                val row = r.build()
                aggRows.add(row)
                messages.add(row.toDto(pending.originalPrice))
            }
        }

        //todo add depuplication here for both rows and messages
        if (aggRows.isNotEmpty()) {
            aggMapper.insertUpdateAgg(aggRows)
        }

        if (messages.isNotEmpty()) {
            sqsProducer.sendMessage(messages)
        }
    }

    override fun leaseLost(leaseLostInput: LeaseLostInput?) {
        log.warn("Lease lost: $leaseLostInput")
    }

    override fun shardEnded(shardEndedInput: ShardEndedInput?) {
        log.info("Shard ended")
        shardEndedInput?.checkpointer()?.checkpoint()
    }

    override fun shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput?) {
        log.info("Shutting down requested")
        shutdownRequestedInput?.checkpointer()?.checkpoint()
    }

    private fun buildConvertRequestItem(
        amount: BigDecimal,
        curCode: Currency,
        asOf: Instant,
        targetCurrency: Currency,
        correlationId: String
    ) = ConvertRequestItem.newBuilder()
        .setSource(
            Money.newBuilder()
                .setAmount(amount.toString())
                .setCurrencyCode(curCode.currencyCode)
                .build()
        )
        .setAsOf(
            Timestamp.newBuilder()
                .setSeconds(asOf.epochSecond)
                .setNanos(asOf.nano)
                .build()
        )
        .setTargetCurrencyCode(targetCurrency.currencyCode)
        .setCorrelationId(correlationId)
        .build()
}