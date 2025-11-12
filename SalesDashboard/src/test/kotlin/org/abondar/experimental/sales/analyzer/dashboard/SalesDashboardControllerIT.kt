package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenueDto
import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenueItemDto
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductRevenueDto
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductsRevenueItemDto
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesDto
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPointDto
import org.abondar.experimental.sales.analyzer.dashboard.testconf.BaseIT
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val res = client.toBlocking().exchange(request, TimeSeriesDto::class.java)

        assertEquals(HttpStatus.OK, res.status)

        val body = res.body()
        assertNotNull(body)

        val points = body.points
        assertTrue(points.isNotEmpty())
        assertEquals(1, points.size)
        assertEquals(agg.productName, points.first().productName)
    }

    @Test
    fun `test get time series handle exception`() {
        val request = HttpRequest.GET<Any>("$apiUrl/time-series/1ff")
            .accept(MediaType.APPLICATION_JSON)

        val ex = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().exchange(request, Array<TimeSeriesPointDto>::class.java)
        }
        assertEquals(HttpStatus.BAD_REQUEST, ex.status)
    }

    @Test
    fun `test get categories`() {
        val request = HttpRequest.GET<Any>("$apiUrl/categories/1m")
            .accept(MediaType.APPLICATION_JSON)
        val res = client.toBlocking().exchange(request, CategoryRevenueDto::class.java)

        assertEquals(HttpStatus.OK, res.status)

        val body = res.body()
        assertNotNull(body)

        val items = body.items
        assertTrue(items.isNotEmpty())
        assertEquals(1, items.size)
        assertEquals(agg.category, items.first().category)
        assertEquals(agg.revenue.toPlainString(), items.first().revenue)
    }


    @Test
    fun `test get products`() {
        val request = HttpRequest.GET<Any>("$apiUrl/products/1m")
            .accept(MediaType.APPLICATION_JSON)
        val res = client.toBlocking().exchange(request, ProductRevenueDto::class.java)

        assertEquals(HttpStatus.OK, res.status)

        val body = res.body()
        assertNotNull(body)

        val items = body.items
        assertTrue(items.isNotEmpty())
        assertEquals(1, items.size)
        assertEquals(agg.productName, items.first().productName)
        assertEquals(agg.revenue.toPlainString(), items.first().revenue)
        assertEquals(agg.units, items.first().units)
        assertEquals(agg.orders, items.first().orders)
    }


}