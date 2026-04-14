package org.abondar.experimental.sales.analyzer.dashboard.data

import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductsRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPoint

@Singleton
open class SalesDashboardService(
    private val salesDashboardMapper: SalesDashboardMapper
) {

    @Cacheable("sales-time-series", parameters = ["period"])
    open fun timeSeriesPeriod(period: String): List<TimeSeriesPoint> =
        salesDashboardMapper.timeSeriesPeriod(period)

    @Cacheable("sales-categories", parameters = ["period", "limit"])
    open fun topCategoriesPerPeriod(period: String, limit: Int): List<CategoryRevenue> =
        salesDashboardMapper.topCategoriesPerPeriod(period, limit)

    @Cacheable("sales-products", parameters = ["period", "limit"])
    open fun topProductsByRevenue(period: String, limit: Int): List<ProductsRevenue> =
        salesDashboardMapper.topProductsByRevenue(period, limit)
}