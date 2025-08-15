package org.abondar.experimental.sales.analyzer.ingestor.client

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import java.net.URI

@Factory
class AwsClientFactory {

    private var client: KinesisAsyncClient? = null

    @Singleton
    fun kinesisClient(
        @Value("\${aws.kinesis.endpoint-override}") endpoint: String,
        @Value("\${aws.region}") region: String,
        @Value("\${aws.access-key-id}") accessKeyId: String,
        @Value("\${aws.secret-access-key}") secretAccessKey: String
    ): KinesisAsyncClient {
        return KinesisAsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(
                DefaultCredentialsProvider.builder()
                    .build()
            )
            .apply {
                if (endpoint.isNotBlank()) {
                    endpointOverride(URI.create(endpoint))
                }
            }
            .build()
    }

    @PreDestroy
    fun close() {
        client?.close()
    }
}