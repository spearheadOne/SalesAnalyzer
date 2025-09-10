package org.abondar.experimental.sales.analyzer.job

import com.fasterxml.jackson.databind.ObjectMapper
import junit.framework.TestCase.assertEquals
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.abondar.experimental.sales.analyzer.job.data.AggTestMapper
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.kinesis.model.Record
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput
import software.amazon.kinesis.processor.Checkpointer
import software.amazon.kinesis.processor.PreparedCheckpointer
import software.amazon.kinesis.processor.RecordProcessorCheckpointer
import software.amazon.kinesis.retrieval.KinesisClientRecord
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.time.Instant


class SalesRecordProcessorIT : BaseIT() {

    @Test
    fun `test processor logic directly`() {
        val aggMapper = applicationContext.getBean(AggMapper::class.java)
        val objectMapper = applicationContext.getBean(ObjectMapper::class.java)
        val testMapper = applicationContext.getBean(AggTestMapper::class.java)

        testMapper.deleteAll()

        val processor = SalesRecordProcessor(objectMapper, aggMapper)

        val now = Instant.now()
        val records = listOf(
            jsonRecord(
                """
            {"timestamp":"${now.plusSeconds(10)}","orderId":"O1","customerId":"C1","productId":"P1",
             "productName":"Mouse","category":"Electronics","price":"10.00","amount":2,"currency":"EUR","region":"DE"}
        """.trimIndent(), 1
            ),
            jsonRecord(
                """
            {"timestamp":"${now.plusSeconds(20)}","orderId":"O2","customerId":"C1","productId":"P1",
             "productName":"Mouse","category":"Electronics","price":"5.00","amount":1,"currency":"EUR","region":"DE"}
        """.trimIndent(), 2
            ),
            jsonRecord(
                """
            {"timestamp":"${now.plusSeconds(70)}","orderId":"O3","customerId":"C1","productId":"P1",
             "productName":"Mouse","category":"Electronics","price":"7.50","amount":3,"currency":"EUR","region":"DE"}
        """.trimIndent(), 3
            )
        )

        val processInput = ProcessRecordsInput.builder()
            .records(records)
            .checkpointer(noopCheckpointer())
            .build()

        processor.processRecords(processInput)

        val res = testMapper.getAggregates()
        assertEquals(3, res.size)
    }

    private fun jsonRecord(json: String, seq: Long): KinesisClientRecord =
        KinesisClientRecord.builder()
            .data(ByteBuffer.wrap(json.toByteArray(StandardCharsets.UTF_8)))
            .partitionKey("pk")
            .sequenceNumber(seq.toString())
            .approximateArrivalTimestamp(Instant.now())
            .build()

    private fun noopCheckpointer(): RecordProcessorCheckpointer = object : RecordProcessorCheckpointer {
        override fun checkpoint() {}
        override fun checkpoint(record: Record?) {}
        override fun checkpoint(sequenceNumber: String?) {}
        override fun checkpoint(sequenceNumber: String?, subSequenceNumber: Long) {}
        override fun prepareCheckpoint(): PreparedCheckpointer {
            TODO("Not yet implemented")
        }

        override fun prepareCheckpoint(applicationState: ByteArray?): PreparedCheckpointer {
            TODO("Not yet implemented")
        }

        override fun prepareCheckpoint(record: Record?): PreparedCheckpointer {
            TODO("Not yet implemented")
        }

        override fun prepareCheckpoint(
            record: Record?,
            applicationState: ByteArray?
        ): PreparedCheckpointer {
            TODO("Not yet implemented")
        }

        override fun prepareCheckpoint(sequenceNumber: String?): PreparedCheckpointer {
            TODO("Not yet implemented")
        }

        override fun prepareCheckpoint(
            sequenceNumber: String?,
            applicationState: ByteArray?
        ): PreparedCheckpointer {
            TODO("Not yet implemented")
        }

        override fun prepareCheckpoint(
            sequenceNumber: String?,
            subSequenceNumber: Long
        ): PreparedCheckpointer? {
            TODO("Not yet implemented")
        }

        override fun prepareCheckpoint(
            sequenceNumber: String?,
            subSequenceNumber: Long,
            applicationState: ByteArray?
        ): PreparedCheckpointer {
            TODO("Not yet implemented")
        }

        override fun checkpointer(): Checkpointer {
            TODO("Not yet implemented")
        }
    }
}