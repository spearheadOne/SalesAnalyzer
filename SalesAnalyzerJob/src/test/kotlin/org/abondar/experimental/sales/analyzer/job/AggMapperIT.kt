package org.abondar.experimental.sales.analyzer.job

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.abondar.experimental.sales.analyzer.job.data.AggTestMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class AggMapperIT : BaseIT() {

    @Test
    fun `test agg mapper save row`() {
        val aggMapper = applicationContext.getBean(AggMapper::class.java)

        testMapper.deleteAll()

        val agg = AggRow(
            Instant.now(), "test", "test", "test",
            1, 1, BigDecimal(10)
        )

        aggMapper.insertUpdateAgg(listOf(agg))

        val res = testMapper.getAggregates()
        assertEquals(1, res.size)
        assertEquals(agg, res.first())
    }


}