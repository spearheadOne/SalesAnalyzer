package org.abondar.experimental.sales.analyzer.job.sink

import org.abondar.experimental.sales.analyzer.job.data.AggMapper

object SalesAggSinkMapperHolder {

    @JvmStatic
    lateinit var mapper: AggMapper
}