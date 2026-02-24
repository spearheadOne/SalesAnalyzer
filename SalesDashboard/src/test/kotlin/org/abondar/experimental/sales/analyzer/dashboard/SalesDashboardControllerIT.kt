package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenueDto
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductRevenueDto
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesDto
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPointDto
import org.abondar.experimental.sales.analyzer.dashboard.testconf.BaseIT
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SalesDashboardControllerIT : BaseIT() {


    @Inject
    @field:Client("/")
    private lateinit var client: HttpClient

    private var apiBase: String = "/dashboard"


    @Test
    fun `test get time series`() {
        val request = HttpRequest.GET<Any>("$apiBase/time-series/1m")
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
        val request = HttpRequest.GET<Any>("$apiBase/time-series/1ff")
            .accept(MediaType.APPLICATION_JSON)

        val ex = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().exchange(request, Array<TimeSeriesPointDto>::class.java)
        }
        assertEquals(HttpStatus.BAD_REQUEST, ex.status)
    }

    @Test
    fun `test get categories`() {
        val request = HttpRequest.GET<Any>("$apiBase/categories/1m")
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
        val request = HttpRequest.GET<Any>("$apiBase/products/1m")
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