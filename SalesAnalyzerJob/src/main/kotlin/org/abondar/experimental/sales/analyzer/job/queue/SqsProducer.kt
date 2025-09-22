package org.abondar.experimental.sales.analyzer.job.queue

import io.micronaut.context.annotation.Value
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggRow
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry

@Singleton
class SqsProducer(
    private val sqsClient: SqsAsyncClient,
    private val objectMapper: ObjectMapper,
    @param:Value("\${aws.sqs.queueUrl:}") private val queueUrl: String
) {
    fun sendMessage(rows: List<AggRow>) {
        rows.chunked(10).forEach { chunk ->
            val entries = chunk.mapIndexed { idx, row ->
                SendMessageBatchRequestEntry.builder()
                    .id(idx.toString())
                    .messageBody(objectMapper.writeValueAsString(row))
                    .build()
            }

            sqsClient.sendMessageBatch(
                SendMessageBatchRequest.builder()
                    .entries(entries)
                    .queueUrl(queueUrl)
                    .build()
            )
        }
    }
}