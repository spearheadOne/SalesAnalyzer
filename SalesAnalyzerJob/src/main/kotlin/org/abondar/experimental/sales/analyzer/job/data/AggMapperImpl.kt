package org.abondar.experimental.sales.analyzer.job.data

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.session.SqlSessionFactory

@Singleton
class AggMapperImpl(private val factory: SqlSessionFactory): AggMapper {
    override fun insertUpdateAgg(batch: List<AggRow>) {
        factory.openSession().use { session->
            val mapper = session.getMapper(AggMapper::class.java)
            mapper.insertUpdateAgg(batch)
            session.commit()

        }
    }

    override fun getAggByProduct(): List<AggRow> {
        factory.openSession().use { session->
            val mapper = session.getMapper(AggMapper::class.java)
            val res = mapper.getAggByProduct()
            session.commit()

            return res
        }
    }

    override fun deleteAll() {
        factory.openSession().use { session->
            val mapper = session.getMapper(AggMapper::class.java)
            val res = mapper.deleteAll()
            session.commit()

            return res
        }
    }


}