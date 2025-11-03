package org.abondar.experimental.sales.analyzer.job.mapper

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.session.SqlSessionFactory

@Singleton
class AggTestMapperImpl(private val factory: SqlSessionFactory) : AggTestMapper {
    override fun getAggregates(): List<AggRow> = execute { it.getAggregates() }

    override fun deleteAll() = execute { it.deleteAll() }

    private inline fun <T> execute(block: (AggTestMapper) -> T): T {
        factory.openSession().use { session ->
            val mapper = session.getMapper(AggTestMapper::class.java)
            val result = block(mapper)
            session.commit()
            return result
        }
    }
}