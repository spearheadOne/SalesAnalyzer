package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.StreamingHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductsRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPoint
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.math.BigDecimal
import java.time.Instant

class SalesDashboardControllerIT : BaseIT() {

    private lateinit var server: EmbeddedServer

    private lateinit var client: HttpClient

    private lateinit var apiUrl: String


    @BeforeEach
    fun startServerAndSetupData() {
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
    }

    @AfterEach
    fun stopServer() {
        if (this::client.isInitialized) client.close()
        if (this::server.isInitialized && server.isRunning) server.stop()
    }

    @Test
    fun `test get time series`() {
        val request = HttpRequest.GET<Any>("$apiUrl/time-series/1m")
            .accept(MediaType.APPLICATION_JSON)
        val res = client.toBlocking().exchange(request, Array<TimeSeriesPoint>::class.java)

        assertEquals(HttpStatus.OK, res.status)

        val body = res.body()
        assertTrue(body.isNotEmpty())
        assertEquals(1, body.size)
        assertEquals(agg.productName, body.first().productName)
    }

    @Test
    fun `test get time series handle exception`() {
        val request = HttpRequest.GET<Any>("$apiUrl/time-series/1ff")
            .accept(MediaType.APPLICATION_JSON)

        val ex = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().exchange(request, Array<TimeSeriesPoint>::class.java)
        }
        assertEquals(HttpStatus.BAD_REQUEST, ex.status)
    }

    @Test
    fun `test get categories`() {
        val request = HttpRequest.GET<Any>("$apiUrl/categories/1m")
            .accept(MediaType.APPLICATION_JSON)
        val res = client.toBlocking().exchange(request, Array<CategoryRevenue>::class.java)

        assertEquals(HttpStatus.OK, res.status)

        val body = res.body()
        assertTrue(body.isNotEmpty())
        assertEquals(1, body.size)
        assertEquals(agg.category, body.first().category)
        assertEquals(agg.revenue, body.first().revenue)
    }


    @Test
    fun `test get products`() {
        val request = HttpRequest.GET<Any>("$apiUrl/products/1m")
            .accept(MediaType.APPLICATION_JSON)
        val res = client.toBlocking().exchange(request, Array<ProductsRevenue>::class.java)

        assertEquals(HttpStatus.OK, res.status)

        val body = res.body()
        assertTrue(body.isNotEmpty())
        assertEquals(1, body.size)
        assertEquals(agg.productName, body.first().productName)
        assertEquals(agg.revenue, body.first().revenue)
        assertEquals(agg.units, body.first().units)
        assertEquals(agg.orders, body.first().orders)
    }


}