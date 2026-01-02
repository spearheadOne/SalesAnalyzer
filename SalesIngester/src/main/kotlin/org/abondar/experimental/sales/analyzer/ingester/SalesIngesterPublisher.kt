package org.abondar.experimental.sales.analyzer.ingester

import io.micronaut.context.annotation.Value
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import kotlinx.coroutines.future.await
import org.abondar.experimental.sales.analyzer.data.SalesRecord
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry

@Singleton
class SalesIngesterPublisher(
    private val mapper: ObjectMapper,
    private val kinesisClient: KinesisAsyncClient
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${aws.services.kinesis.stream}")
    lateinit var streamName: String

    suspend fun publishMessage(batch: List<SalesRecord>) {
        val entries = batch.map { record ->
            val data = mapper.writeValueAsBytes(record)
            PutRecordsRequestEntry.builder()
                .data(SdkBytes.fromByteArray(data))
                .partitionKey(record.productId)
                .build()
        }

        val req = PutRecordsRequest.builder()
            .streamName(streamName)
            .records(entries)
            .build()

        var resp = kinesisClient.putRecords(req).await()
        val failed = resp.failedRecordCount() ?: 0
        if (failed > 0) {
            log.warn("Failed to publish $failed records")
        }
    }

}