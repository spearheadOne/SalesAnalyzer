package org.abondar.experimental.sales.analyzer.dashboard.data

import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductsRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPoint
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.Instant

@Mapper
interface SalesDashboardMapper {
    fun timeSeriesPeriod(@Param("period") period: String): List<TimeSeriesPoint>

    fun topCategoriesPerPeriod(@Param("period") period: String, @Param("limit") limit: Int): List<CategoryRevenue>

    fun topProductsByRevenue(@Param("period") period: String, @Param("limit") limit: Int): List<ProductsRevenue>

    fun timeSeriesSince (@Param("since") since: Instant): List<TimeSeriesPoint>
}