package org.abondar.experimental.sales.analyzer.job.data

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.annotations.Delete


interface AggMapper {

    fun insertUpdateAgg(batch: List<AggRow>)

    fun getAggregates(): List<AggRow>

    @Delete("delete from sales_agg")
    fun deleteAll()
}