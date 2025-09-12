package org.abondar.experimental.sales.analyzer.job.data

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.ibatis.annotations.Mapper

@Mapper
interface AggMapper {

    fun insertUpdateAgg(batch: List<AggRow>)

}