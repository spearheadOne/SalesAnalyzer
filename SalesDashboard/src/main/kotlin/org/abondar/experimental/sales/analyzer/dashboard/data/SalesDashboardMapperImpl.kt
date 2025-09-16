package org.abondar.experimental.sales.analyzer.dashboard.data

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductsRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPoint
import org.apache.ibatis.session.SqlSessionFactory
import java.time.Instant

@Singleton
class SalesDashboardMapperImpl(private val factory: SqlSessionFactory) : SalesDashboardMapper {
    override fun timeSeriesPeriod(period: String): List<TimeSeriesPoint> = execute {
        it.timeSeriesPeriod(period)
    }

    override fun topCategoriesPerPeriod(
        period: String,
        limit: Int
    ): List<CategoryRevenue> = execute {
        it.topCategoriesPerPeriod(period, limit)
    }

    override fun topProductsByRevenue(
        period: String,
        limit: Int
    ): List<ProductsRevenue> = execute {
        it.topProductsByRevenue(period, limit)
    }

    override fun timeSeriesSince(since: Instant): List<TimeSeriesPoint> = execute {
        it.timeSeriesSince(since)
    }


    private inline fun <T> execute(block: (SalesDashboardMapper) -> T): T {
        factory.openSession().use { session ->
            val mapper = session.getMapper(SalesDashboardMapper::class.java)
            val result = block(mapper)
            session.commit()
            return result
        }
    }
}