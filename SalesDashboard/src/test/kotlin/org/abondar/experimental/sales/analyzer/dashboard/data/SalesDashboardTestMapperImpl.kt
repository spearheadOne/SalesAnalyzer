package org.abondar.experimental.sales.analyzer.dashboard.data

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.session.SqlSessionFactory

@Singleton
class SalesDashboardTestMapperImpl(private val factory: SqlSessionFactory) : SalesDashboardTestMapper {
    override fun insertAgg(agg: AggRow) = execute { it.insertAgg(agg) }

    override fun deleteAll() = execute { it.deleteAll() }

    private inline fun <T> execute(block: (SalesDashboardTestMapper) -> T): T {
        factory.openSession().use { session ->
            val mapper = session.getMapper(SalesDashboardTestMapper::class.java)
            val result = block(mapper)
            session.commit()
            return result
        }
    }
}