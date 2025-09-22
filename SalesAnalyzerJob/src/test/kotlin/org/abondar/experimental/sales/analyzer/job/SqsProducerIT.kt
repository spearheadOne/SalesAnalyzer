package org.abondar.experimental.sales.analyzer.job

import io.micronaut.serde.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.job.queue.SqsProducer
import org.abondar.experimental.sales.analyzer.job.testconf.BaseIT
import org.abondar.experimental.sales.analyzer.job.testconf.Containers
import org.abondar.experimental.sales.analyzer.job.testconf.Properties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.TimeUnit

class SqsProducerIT : BaseIT() {

    override fun extraProperties(): Map<String, Any?> = Properties.localstackAws(Containers.LOCALSTACK)

    @Test
    fun `test sending agg row to sqs`() {
        val sqsClient = applicationContext.getBean(SqsAsyncClient::class.java)
        val objectMapper = applicationContext.getBean(ObjectMapper::class.java)

        val queueUrl = sqsClient.createQueue(
            CreateQueueRequest.builder()
                .queueName("sales-queue").build()
        ).get(5, TimeUnit.SECONDS).queueUrl()


        val sqsProducer = SqsProducer(sqsClient, objectMapper, queueUrl)

        val agg = AggRow(
            Instant.now(), "test", "test", "test", 1, 1, BigDecimal(10)
        )

        runBlocking {
            sqsProducer.sendMessage(listOf(agg))
        }

        val msg = sqsClient.receiveMessage(
            ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(2)
                .build()
        ).get().messages()

        assertNotNull(msg)
        assertEquals(1, msg.size)
        assertNotNull(msg.first().body())
        assertTrue(msg.first().body().contains(agg.productName))

    }
}