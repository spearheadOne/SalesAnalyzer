package org.abondar.experimental.sales.analyzer.job.sink

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.apache.flink.api.connector.sink2.Sink
import org.apache.flink.api.connector.sink2.SinkWriter
import org.apache.flink.api.connector.sink2.WriterInitContext


class SalesAggSink() : Sink<AggRow> {

    companion object {
        private const val BATCH_SIZE = 500
    }

    override fun createWriter(context: WriterInitContext?): SinkWriter<AggRow> {
        return SalesAggSinkWriter(SalesAggSinkMapperHolder.mapper, BATCH_SIZE)
    }

}