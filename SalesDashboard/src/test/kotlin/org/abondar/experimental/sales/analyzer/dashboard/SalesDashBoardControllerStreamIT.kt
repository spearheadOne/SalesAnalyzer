package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.sse.SseClient
import io.micronaut.http.sse.Event
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.dashboard.stream.Feed
import org.abondar.experimental.sales.analyzer.dashboard.testconf.BaseIT
import org.abondar.experimental.sales.analyzer.data.AggDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant

class SalesDashBoardControllerStreamIT : BaseIT() {

    private var apiBase: String = "/dashboard"

    @Inject
    @field:Client("/")
    private lateinit var sseClient: SseClient

    @Inject
    private lateinit var feed: Feed

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

        feed.emit(newAgg)

        val req = HttpRequest.GET<Any>("$apiBase/stream")
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