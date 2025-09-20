package org.abondar.experimental.sales.analyzer.job

import org.abondar.experimental.sales.analyzer.job.testconf.BaseIT
import org.abondar.experimental.sales.analyzer.job.testconf.Containers
import org.abondar.experimental.sales.analyzer.job.testconf.Properties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest
import java.time.Instant
import java.util.concurrent.TimeUnit


class SalesJobAnalyzerIT: BaseIT(){

    private val streamName = "sales-stream"

    lateinit var kinesisClient: KinesisAsyncClient

    override fun extraProperties(): Map<String, Any?> = Properties.localstackAws(Containers.LOCALSTACK) + mapOf(
        "aws.services.kinesis.stream" to streamName,
    )

    @BeforeEach
    fun setup() {
        kinesisClient = applicationContext.getBean(KinesisAsyncClient::class.java)
        testMapper.deleteAll()

        kinesisClient.createStream(
            CreateStreamRequest.builder()
                .streamName(streamName)
                .shardCount(1)
                .build()
        )
    }

    @Test
    fun `test analyzer job`() {
        val job = applicationContext.getBean(SalesAnalyzerJob::class.java)

        job.run()
        Thread.sleep(5000)

        val now = Instant.now()
        putRecord(
            """
            {"timestamp":"${now.plusSeconds(10)}","orderId":"O1","customerId":"C1","productId":"P1",
             "productName":"Mouse","category":"Electronics","price":"10.00","amount":2,"currency":"EUR","region":"DE"}
        """.trimIndent()
        )

        putRecord(
            """
            {"timestamp":"${now.plusSeconds(20)}","orderId":"O2","customerId":"C1","productId":"P1",
             "productName":"Mouse","category":"Electronics","price":"5.00","amount":1,"currency":"EUR","region":"DE"}
        """.trimIndent()
        )

        putRecord(
            """
            {"timestamp":"${now.plusSeconds(70)}","orderId":"O3","customerId":"C1","productId":"P1",
             "productName":"Mouse","category":"Electronics","price":"7.50","amount":3,"currency":"EUR","region":"DE"}
        """.trimIndent()
        )

        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20)
        var processedCount: Int

        do {
            val rows = testMapper.getAggregates()
            processedCount = rows.size
            if (processedCount >= 3) break
            Thread.sleep(500)
        } while (System.nanoTime() < deadline)

        job.stop()

        val res = testMapper.getAggregates()
        assertEquals(processedCount, res.size)

    }


    private fun putRecord(json: String) {
        kinesisClient.putRecord(
            PutRecordRequest.builder()
                .streamName(streamName)
                .partitionKey("pk")
                .data(SdkBytes.fromUtf8String(json))
                .build()
        )
    }

}