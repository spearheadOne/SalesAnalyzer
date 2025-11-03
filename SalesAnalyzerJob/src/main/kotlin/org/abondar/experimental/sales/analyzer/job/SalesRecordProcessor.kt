package org.abondar.experimental.sales.analyzer.job

import com.google.protobuf.Timestamp
import io.micronaut.serde.ObjectMapper
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.abondar.experimental.sales.analyzer.data.AggRow
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
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class SalesRecordProcessor(
    private val objectMapper: ObjectMapper,
    private val aggMapper: AggMapper,
    private val sqsProducer: SqsProducer,
    private val fxClient: FxClient,
    private val defaultCurrency: String
) : ShardRecordProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun initialize(initInput: InitializationInput?) {
        log.info("Initializing record processor with shard ID: ${initInput?.shardId()}")
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput?) {
        var currency: Currency
        val defCur = Currency.getInstance(defaultCurrency)
        val aggRows = mutableListOf<AggRow>()
        val fxRows = mutableMapOf<String, AggRow.Builder>()
        val convertRequestBatch = mutableListOf<ConvertRequestItem>()

        processRecordsInput?.records()?.forEach { rec ->
            try {
                val recBytes = rec.data().asReadOnlyBuffer().let { buffer ->
                    ByteArray(buffer.remaining()).apply { buffer.get(this) }
                }
                val salesRecord = objectMapper.readValue(recBytes, SalesRecord::class.java)

                currency = salesRecord.currency

                val row = AggRow.builder().apply {
                    eventTime = salesRecord.timestamp
                    productName = salesRecord.productName
                    productId = salesRecord.productId
                    units = salesRecord.amount.toLong()
                    category = salesRecord.category
                    currency = defCur.currencyCode.let { Currency.getInstance(it) }
                }

                if (currency == defCur) {
                    row.revenue = salesRecord.price.multiply(BigDecimal(salesRecord.amount))
                    aggRows.add(row.build())
                } else {
                    val correlationId = UUID.randomUUID().toString()
                    convertRequestBatch.add(
                        buildConvertRequestItem(
                            salesRecord.price, currency,
                            salesRecord.timestamp, defCur,
                            correlationId
                        )
                    )
                    fxRows[correlationId] = row
                }

            } catch (ex: Exception) {
                log.error("Error processing record", ex)
            }
        }

        if (convertRequestBatch.isNotEmpty()) {
            val convertBatchResponse = runBlocking {
                withTimeout(800.milliseconds) {
                    fxClient.convertBatch(
                        ConvertBatchRequest.newBuilder()
                            .addAllItems(convertRequestBatch)
                            .build()
                    )
                }
            }

            convertBatchResponse.itemsList.forEach {res ->
                val row = fxRows[res.correlationId]!!

                val convertedPrice = res.converted.amount.toBigDecimal()
                row.revenue = convertedPrice.multiply(BigDecimal(row.units))
                aggRows.add(row.build())
            }
        }

        if (aggRows.isNotEmpty()) {
            aggMapper.insertUpdateAgg(aggRows)
            sqsProducer.sendMessage(aggRows)
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
                .setNanos(asOf.nano)
                .build()
        )
        .setTargetCurrencyCode(targetCurrency.currencyCode)
        .setCorrelationId(correlationId)
        .build()
}