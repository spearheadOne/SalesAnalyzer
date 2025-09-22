package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.StreamingHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.serde.ObjectMapper
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

@Testcontainers(disabledWithoutDocker = true)
class SalesDashBoardControllerStreamIT {
    private lateinit var applicationContext: ApplicationContext

    private lateinit var server: EmbeddedServer

    private lateinit var client: HttpClient

    private lateinit var apiUrl: String

    private lateinit var sqsClient: SqsAsyncClient

    private lateinit var objectMapper: ObjectMapper

    companion object {
        @JvmStatic
        val localstack: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
                .withServices(LocalStackContainer.Service.SQS)
                .withReuse(true)
    }

    @BeforeEach
    fun setup() {
        localstack.start()

        applicationContext = ApplicationContext.run(
            PropertySource.of(
                "test", mapOf(
                    "aws.region" to localstack.region,
                    "aws.access-key-id" to localstack.accessKey,
                    "aws.secret-access-key" to localstack.secretKey,
                    "aws.services.sqs.endpoint-override" to localstack
                        .getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
                    "micronaut.jms.sqs.enabled" to "true"
                )
            )
        )

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

        objectMapper = applicationContext.getBean(ObjectMapper::class.java)
    }


    @AfterEach
    fun shutdown() {
        if (this::client.isInitialized) client.close()
        if (this::server.isInitialized && server.isRunning) server.stop()

        if (this::applicationContext.isInitialized) {
            applicationContext.close()
        }
    }

    @Test
    fun `test streaming`() {
        val streamingClient = StreamingHttpClient.create(server.url)
        val req = HttpRequest.GET<Any>("$apiUrl/stream")
            .accept(MediaType.APPLICATION_JSON_STREAM)
        val publisher = streamingClient.jsonStream(req, AggRow::class.java)

        val queueUrl = sqsClient.createQueue(
            CreateQueueRequest.builder()
                .queueName("sales-queue").build()
        ).get(5, TimeUnit.SECONDS).queueUrl()

        val newAgg = AggRow(
            Instant.now(),
            "test-stream", "test-stream", "test",
            1, 1, BigDecimal.TEN
        )

        val item = Mono.fromFuture(
            sqsClient.sendMessage(
                SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(newAgg))
                    .build()
            )
        )
            .thenMany(Flux.from(publisher))
            .timeout(Duration.ofSeconds(10))
            .blockFirst()

        assertNotNull(item)
        assertEquals("test-stream", item!!.productName)
    }
}