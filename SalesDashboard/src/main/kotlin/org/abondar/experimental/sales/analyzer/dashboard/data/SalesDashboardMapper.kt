package org.abondar.experimental.sales.analyzer.dashboard.data

import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductsRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPoint
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SalesDashboardMapper {
    fun timeSeriesPeriod(period: String): List<TimeSeriesPoint>

    fun topCategoriesPerPeriod(period: String, limit: Int): List<CategoryRevenue>

    fun topProductsByRevenue(period: String, limit: Int): List<ProductsRevenue>
}