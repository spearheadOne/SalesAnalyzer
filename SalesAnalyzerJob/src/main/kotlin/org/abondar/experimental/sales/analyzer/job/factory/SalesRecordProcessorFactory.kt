package org.abondar.experimental.sales.analyzer.job.factory

import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.job.SalesRecordProcessor
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.abondar.experimental.sales.analyzer.job.queue.SqsProducer
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

@Singleton
class SalesRecordProcessorFactory(
    private  val aggMapper: AggMapper,
    private val objectMapper: ObjectMapper,
    private val  sqsProducer: SqsProducer,
): ShardRecordProcessorFactory {

    override fun shardRecordProcessor(): ShardRecordProcessor? {
        return SalesRecordProcessor(objectMapper, aggMapper, sqsProducer)
    }
}