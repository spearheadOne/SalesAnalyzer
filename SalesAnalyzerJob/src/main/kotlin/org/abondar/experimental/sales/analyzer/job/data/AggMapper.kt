package org.abondar.experimental.sales.analyzer.job.data

import org.abondar.experimental.sales.analyzer.data.AggRow


interface AggMapper {

    fun insertUpdateAgg(batch: List<AggRow>)

}