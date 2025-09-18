package org.abondar.experimental.sales.analyzer.dashboard.data


import org.abondar.experimental.sales.analyzer.dashboard.BaseIT
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class SalesDashboardMapperIT : BaseIT() {

    lateinit var dashboardMapper: SalesDashboardMapper

    @BeforeEach
    fun initMapper() {
        dashboardMapper = applicationContext.getBean(SalesDashboardMapper::class.java)
    }

    @Test
    fun `test time series period`() {
        val timeSeriesPoints = dashboardMapper.timeSeriesPeriod("1 minutes")
        assertEquals(timeSeriesPoints.size, 1)
        assertEquals(timeSeriesPoints.first().productName, "test")
    }

    @Test
    fun `test time series since`() {
        val timeSeriesPoints = dashboardMapper.timeSeriesSince(Instant.now().minusSeconds(60))
        assertEquals(timeSeriesPoints.size, 1)
    }

    @Test
    fun `test categories per period`() {
        val categories = dashboardMapper.topCategoriesPerPeriod("1 minutes", 2)
        assertEquals(categories.size, 1)
        assertEquals(categories.first().category, "test")
    }

    @Test
    fun `test products by revenue`() {
        val productsRevenue = dashboardMapper.topProductsByRevenue("1 minutes", 2)
        assertEquals(productsRevenue.size, 1)
        assertEquals(productsRevenue.first().productName, "test")
    }


}