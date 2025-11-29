package org.abondar.experimental.sales.analyzer.dashboard.health

import io.micronaut.context.annotation.Value
import io.micronaut.health.HealthStatus
import io.micronaut.management.health.indicator.HealthIndicator
import io.micronaut.management.health.indicator.HealthResult
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName

@Singleton
class SqsHealthIndicator(
    private val sqsClient: SqsAsyncClient,
    @param:Value("\${aws.services.sqs.queueUrl:}") private val queueUrl: String
) : HealthIndicator {
    override fun getResult(): Publisher<HealthResult?>? {
        val req = GetQueueAttributesRequest.builder()
            .queueUrl(queueUrl)
            .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
            .build()

        return Mono.fromCompletionStage(sqsClient.getQueueAttributes(req))
            .map { resp ->
                val countStr = resp.attributes()[QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES]
                val count = countStr?.toLongOrNull() ?: 0L

                val status = if (count >= 0) HealthStatus.UP else HealthStatus.UNKNOWN

                HealthResult.builder("sqs-queue", status)
                    .details(
                        mapOf(
                            "queueUrl" to queueUrl,
                            "approxMessages" to count
                        )
                    )
                    .build()
            }
            .onErrorResume { e ->
                Mono.just(
                    HealthResult.builder("sqs-queue", HealthStatus.DOWN)
                        .exception(e)
                        .details(
                            mapOf(
                                "queueUrl" to queueUrl,
                                "error" to e.message
                            )
                        )
                        .build()
                )
            }
    }
}