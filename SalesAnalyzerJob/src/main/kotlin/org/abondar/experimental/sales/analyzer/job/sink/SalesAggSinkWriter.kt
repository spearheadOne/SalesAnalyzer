package org.abondar.experimental.sales.analyzer.job.sink

import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.apache.flink.api.connector.sink2.SinkWriter
import java.io.IOException

class SalesAggSinkWriter(
    private val aggMapper: AggMapper,
    private val batchSize: Int = 500
) : SinkWriter<AggRow> {

    private var batch = ArrayList<AggRow>(batchSize)


    override fun write(
        element: AggRow?,
        context: SinkWriter.Context?
    ) {
        batch.add(element!!)

        if (batch.size >= batchSize) {
            doFlush()
        }
    }


    @Synchronized
    @Throws(IOException::class, InterruptedException::class)
    override fun flush(endOfInput: Boolean) {
        doFlush()
    }

    @Synchronized
    override fun close() {
        doFlush()
    }


    private fun doFlush() {
        if (batch.isNotEmpty()) {
            aggMapper.insertUpdateAgg(batch)
            batch.clear()
        }
    }

}