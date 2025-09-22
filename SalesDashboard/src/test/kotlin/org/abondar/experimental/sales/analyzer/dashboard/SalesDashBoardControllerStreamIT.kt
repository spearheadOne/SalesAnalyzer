package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.StreamingHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.serde.ObjectMapper
import org.abondar.experimental.sales.analyzer.dashboard.testconf.BaseIT
import org.abondar.experimental.sales.analyzer.dashboard.testconf.Containers
import org.abondar.experimental.sales.analyzer.dashboard.testconf.Properties
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

class SalesDashBoardControllerStreamIT : BaseIT() {

    private lateinit var server: EmbeddedServer

    private lateinit var client: HttpClient

    private lateinit var apiUrl: String

    private lateinit var sqsClient: SqsAsyncClient

    override fun extraProperties(): Map<String, Any?> = Properties.localstackAws(Containers.LOCALSTACK)


    @BeforeEach
    fun initClients() {
        server = applicationContext.getBean(EmbeddedServer::class.java)
        if (!server.isRunning) {
            server.start()
            apiUrl = server.url.toString() + "/dashboard"
        }

        client = applicationContext.getBean(HttpClient::class.java)
        if (!client.isRunning) {
            client = HttpClient.create(server.url)
            client.start()
        }

        sqsClient = applicationContext.getBean(SqsAsyncClient::class.java)
    }


    @AfterEach
    fun closeClients() {
        if (this::client.isInitialized) client.close()
        if (this::server.isInitialized && server.isRunning) server.stop()

    }

    //todo fix timeout when no containers are created
    @Test
    fun `test streaming`() {
        Thread.sleep(15000)
        val objectMapper = applicationContext.getBean(ObjectMapper::class.java)
        val queueUrl = sqsClient.createQueue(
            CreateQueueRequest.builder()
                .queueName("sales-queue").build()
        ).get(5, TimeUnit.SECONDS).queueUrl()

        val newAgg = AggRow(
            Instant.now(),
            "test-stream", "test-stream", "test",
            1, 1, BigDecimal.TEN
        )

        sqsClient.sendMessage(
            SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(objectMapper.writeValueAsString(newAgg))
                .build()
        ).get(10, TimeUnit.SECONDS)

        Thread.sleep(15000)

        val clientConfiguration = DefaultHttpClientConfiguration()
        clientConfiguration.setReadTimeout(Duration.ofSeconds(200))
        clientConfiguration.setConnectTimeout(Duration.ofSeconds(30))

        val streamingClient = StreamingHttpClient.create(server.url,clientConfiguration)
        val req = HttpRequest.GET<Any>("$apiUrl/stream")
            .accept(MediaType.APPLICATION_JSON_STREAM)
        val publisher = streamingClient.jsonStream(req, AggRow::class.java)

        val item = Flux.from(publisher)
            .timeout(Duration.ofSeconds(60))
            .blockFirst()

        assertNotNull(item)
        assertEquals("test-stream", item!!.productName)
    }
}