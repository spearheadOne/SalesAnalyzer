package org.abondar.experimental.sales.analyzer.job.factory

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.job.SalesRecordProcessor
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

@Singleton
class SalesRecordProcessorFactory(
    private  val aggMapper: AggMapper,
    private val objectMapper: ObjectMapper
): ShardRecordProcessorFactory {

    override fun shardRecordProcessor(): ShardRecordProcessor? {
        return SalesRecordProcessor(objectMapper, aggMapper)
    }
}