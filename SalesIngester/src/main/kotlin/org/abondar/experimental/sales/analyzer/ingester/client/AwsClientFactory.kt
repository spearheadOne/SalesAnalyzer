package org.abondar.experimental.sales.analyzer.ingester.client

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Factory
class AwsClientFactory(
    @param:Value("\${aws.region:us-east-1}") private val region: String,
    @param:Value("\${aws.access-key-id:}") private val accessKeyId: String?,
    @param:Value("\${aws.secret-access-key:}") private val secretAccessKey: String?,
    @param:Value("\${aws.services.kinesis.endpoint-override:}") private val kinesisEndpoint: String,
    @param:Value("\${aws.services.s3.endpoint-override:}") private val s3Endpoint: String,
) {

    private var kinesisClient: KinesisAsyncClient? = null

    @Singleton
    fun kinesisClient(): KinesisAsyncClient {
        return KinesisAsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(resolveCredentialsProvider(kinesisEndpoint, accessKeyId, secretAccessKey))
            .apply {
                if (kinesisEndpoint.isNotBlank()) {
                    endpointOverride(URI.create(kinesisEndpoint))
                }
            }
            .build()
            .also { kinesisClient = it }
    }

    fun resolveCredentialsProvider(
        endpointOverride: String?,
        accessKeyId: String?,
        secretAccessKey: String?
    ): AwsCredentialsProvider {
        return if (!endpointOverride.isNullOrBlank()) {
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
            )
        } else {
            DefaultCredentialsProvider.builder().build()
        }
    }

    @PreDestroy
    fun close() {
        kinesisClient?.close()
    }
}