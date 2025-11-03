package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.StreamingHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.abondar.experimental.sales.analyzer.dashboard.stream.Feed
import org.abondar.experimental.sales.analyzer.dashboard.testconf.BaseIT
import org.abondar.experimental.sales.analyzer.data.AggDto
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

class SalesDashBoardControllerStreamIT : BaseIT() {

    private lateinit var server: EmbeddedServer

    private lateinit var apiUrl: String

    private lateinit var streamingClient: StreamingHttpClient

    @BeforeEach
    fun init() {
        server = applicationContext.getBean(EmbeddedServer::class.java)
        if (!server.isRunning) {
            server.start()
            apiUrl = server.url.toString() + "/dashboard"
        }

        streamingClient = applicationContext.getBean(StreamingHttpClient::class.java)
        if (!streamingClient.isRunning) {
            streamingClient = StreamingHttpClient.create(server.url)
            streamingClient.start()
        }
      }


    @AfterEach
    fun stop() {
        if (this::streamingClient.isInitialized) streamingClient.close()
        if (this::server.isInitialized && server.isRunning) server.stop()

    }

    @Test
    fun `test streaming`() {
        val newAgg = AggDto(
            Instant.now(),
            "test-stream", "test-stream", "test",
            1, 1, "10.0","EUR")
        val feed = applicationContext.getBean(Feed::class.java)
        feed.emit(newAgg)

       val req = HttpRequest.GET<Any>("$apiUrl/stream")
            .accept(MediaType.APPLICATION_JSON_STREAM)
        val publisher = streamingClient.jsonStream(req, AggRow::class.java)

        val item = Flux.from(publisher)
            .timeout(Duration.ofSeconds(20))
            .blockFirst()

        assertNotNull(item)
        assertEquals("test-stream", item!!.productName)
    }
}