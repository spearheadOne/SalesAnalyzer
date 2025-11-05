package org.abondar.experimental.sales.analyzer.job.factory

import io.micronaut.context.annotation.Value
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.job.SalesRecordProcessor
import org.abondar.experimental.sales.analyzer.job.fx.FxClient
import org.abondar.experimental.sales.analyzer.job.mapper.AggMapper
import org.abondar.experimental.sales.analyzer.job.queue.SqsProducer
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

@Singleton
class SalesRecordProcessorFactory(
    private val aggMapper: AggMapper,
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SqsProducer,
    private val fxClient: FxClient,
    @param:Value("\${default-currency:}") private val defaultCurrency: String
) : ShardRecordProcessorFactory {

    override fun shardRecordProcessor(): ShardRecordProcessor? {
        return SalesRecordProcessor(objectMapper, aggMapper, sqsProducer, fxClient, defaultCurrency)
    }
}