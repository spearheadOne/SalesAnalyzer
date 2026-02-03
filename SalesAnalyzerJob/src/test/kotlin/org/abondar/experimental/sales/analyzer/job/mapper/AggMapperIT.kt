package org.abondar.experimental.sales.analyzer.job.mapper

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.job.testconf.BaseIT
import org.junit.jupiter.api.Assertions
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
            1, 1, BigDecimal(10), "EUR")

        aggMapper.insertUpdateAgg(listOf(agg))

        val res = testMapper.getAggregates()
        Assertions.assertEquals(1, res.size)
        Assertions.assertEquals(agg.productName, res.first().productName)
    }


}