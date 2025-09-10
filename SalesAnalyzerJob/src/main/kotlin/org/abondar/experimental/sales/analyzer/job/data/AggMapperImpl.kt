package org.abondar.experimental.sales.analyzer.job.data

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.session.SqlSessionFactory

@Singleton
class AggMapperImpl(private val factory: SqlSessionFactory) : AggMapper {

    override fun insertUpdateAgg(batch: List<AggRow>) {
        factory.openSession().use { session ->
            val mapper = session.getMapper(AggMapper::class.java)
            mapper.insertUpdateAgg(batch)
            session.commit()
        }
    }

}