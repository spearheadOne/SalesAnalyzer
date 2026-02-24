package org.abondar.experimental.sales.analyzer.job

import io.micronaut.context.annotation.Value
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.job.testconf.BaseIT
import org.abondar.experimental.sales.analyzer.job.testconf.Properties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.time.Instant
import java.util.concurrent.TimeUnit


class SalesAnalyzerJobIT: BaseIT(){

    private val streamName = "sales-stream-${System.currentTimeMillis()}-${(1000..9999).random()}"

    @Inject
    private lateinit var kinesisClient: KinesisAsyncClient

    @Inject
    private lateinit var sqsClient: SqsAsyncClient

    @Inject
    private lateinit var job: SalesAnalyzerJob

    @Value("\${aws.services.sqs.queueName}")
    private  lateinit var queueName: String

    lateinit var queueUrl: String

    override fun extraProperties(): Map<String, Any?> = Properties.localstackAws(LOCALSTACK) + mapOf(
        "aws.services.kinesis.stream" to streamName,
        "grpc.channels.fx.address" to "fx-service:9028"

    )

    @BeforeEach
    fun setup() {
        kinesisClient.createStream(
            CreateStreamRequest.builder()
                .streamName(streamName)
                .shardCount(1)
                .build()
        )

        queueUrl = sqsClient.createQueue(
            CreateQueueRequest.builder()
                .queueName(queueName).build()
        ).get(5, TimeUnit.SECONDS).queueUrl()

    }

    @Test
    fun `test analyzer job`() {
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
             "productName":"Mouse","category":"Electronics","price":"5.00","amount":1,"currency":"USD","region":"DE"}
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

        val msg = sqsClient.receiveMessage(
            ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(2)
                .build()
        ).get().messages()

        assertNotNull(msg)

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