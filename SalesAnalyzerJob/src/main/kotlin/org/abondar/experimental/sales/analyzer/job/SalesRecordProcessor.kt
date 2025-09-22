package org.abondar.experimental.sales.analyzer.job

import io.micronaut.serde.ObjectMapper
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.data.SalesRecord
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.abondar.experimental.sales.analyzer.job.queue.SqsProducer
import org.slf4j.LoggerFactory
import software.amazon.kinesis.lifecycle.events.*
import software.amazon.kinesis.processor.ShardRecordProcessor
import java.math.BigDecimal
import java.time.Instant

class SalesRecordProcessor(
    private val objectMapper: ObjectMapper,
    private val aggMapper: AggMapper,
    private val sqsProducer: SqsProducer
) : ShardRecordProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun initialize(initInput: InitializationInput?) {
        log.info("Initializing record processor with shard ID: ${initInput?.shardId()}")
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput?) {

        var orders = 0L
        var units = 0L
        var revenue = BigDecimal.ZERO
        var productId = ""
        var productName = ""
        var category = ""

        val aggRows = mutableListOf<AggRow>()
        processRecordsInput?.records()?.forEach { rec ->
            try {
                val recBytes = rec.data().asReadOnlyBuffer().let { buffer ->
                    ByteArray(buffer.remaining()).apply { buffer.get(this) }
                }
                val salesRecord = objectMapper.readValue(recBytes, SalesRecord::class.java)

                orders += 1
                units += salesRecord.amount.toLong()
                revenue = revenue.add(salesRecord.price.multiply(BigDecimal(salesRecord.amount)))
                productName = salesRecord.productName
                productId = salesRecord.productId
                category = salesRecord.category
                val eventTime = Instant.ofEpochMilli(salesRecord.timestamp.toEpochMilli())

                aggRows += AggRow(
                    eventTime,
                    productId,
                    productName,
                    category,
                    orders,
                    units,
                    revenue
                )
            } catch (ex: Exception) {
                log.error("Error processing record", ex)
            }
        }

        if (!aggRows.isEmpty()) {
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
}