package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.StreamingHttpClient
import io.micronaut.http.client.sse.SseClient
import io.micronaut.http.sse.Event
import io.micronaut.runtime.server.EmbeddedServer
import org.abondar.experimental.sales.analyzer.dashboard.stream.Feed
import org.abondar.experimental.sales.analyzer.dashboard.testconf.BaseIT
import org.abondar.experimental.sales.analyzer.data.AggDto
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

class SalesDashBoardControllerStreamIT : BaseIT() {

    private lateinit var server: EmbeddedServer

    private lateinit var apiUrl: String

    private lateinit var sseClient: SseClient

    @BeforeEach
    fun init() {
        server = applicationContext.getBean(EmbeddedServer::class.java)
        if (!server.isRunning) {
            server.start()
        }
        apiUrl = server.url.toString() + "/dashboard"

        sseClient = SseClient.create(server.url)
    }

    @AfterEach
    fun stop() {
        if (this::server.isInitialized && server.isRunning) {
            server.stop()
        }
    }

    @Test
    fun `test streaming`() {
        val newAgg = AggDto(
            Instant.now(),
            "test-stream",
            "test-stream",
            "test",
            1,
            1,
            "10.0",
            "EUR"
        )

        val feed = applicationContext.getBean(Feed::class.java)
        feed.emit(newAgg)

        val req = HttpRequest.GET<Any>("$apiUrl/stream")
            .accept(MediaType.TEXT_EVENT_STREAM)

        val publisher: Publisher<Event<AggDto>> =
            sseClient.eventStream(req, AggDto::class.java)

        val event = Flux.from(publisher)
            .timeout(Duration.ofSeconds(20))
            .blockFirst()

        assertNotNull(event, "No SSE event received")
        val item = event!!.data
        assertNotNull(item, "Event data is null")
        assertEquals("test-stream", item.productName)
    }
}