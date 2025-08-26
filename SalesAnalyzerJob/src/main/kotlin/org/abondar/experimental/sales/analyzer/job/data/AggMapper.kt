package org.abondar.experimental.sales.analyzer.job.data

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Select


interface AggMapper {

    fun insertUpdateAgg(batch: List<AggRow>)

    @Select("select * from sales_agg")
    fun getAggByProduct(): List<AggRow>

    @Delete("delete from sales_agg")
    fun deleteAll()
}