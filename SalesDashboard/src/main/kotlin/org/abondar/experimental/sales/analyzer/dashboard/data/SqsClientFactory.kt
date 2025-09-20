package org.abondar.experimental.sales.analyzer.dashboard.data

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Factory
class SqsClientFactory(
    @param:Value("\${aws.region:us-east-1}") private val region: String,
    @param:Value("\${aws.access-key-id:}") private val accessKeyId: String?,
    @param:Value("\${aws.secret-access-key:}") private val secretAccessKey: String?,
    @param:Value("\${aws.services.sqs.endpoint-override:}") private val sqsEndpoint: String

) {

    private var sqsAsyncClient: SqsAsyncClient? = null

    @Singleton
    @Requires(missingBeans = [SqsAsyncClient::class])
    fun sqsAsyncClient(): SqsAsyncClient = SqsAsyncClient.builder()
        .region(Region.of(region))
        .credentialsProvider(
            if (sqsEndpoint.isNotBlank()) {
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId ?: "test", secretAccessKey ?: "test")
                )
            } else {
                DefaultCredentialsProvider.builder().build()
            }
        )
        .apply {
            if (sqsEndpoint.isNotBlank()) {
                endpointOverride(URI.create(sqsEndpoint))
            }
        }
        .build()
        .also { sqsAsyncClient = it }


    @PreDestroy
    fun close() {
        sqsAsyncClient?.close()
    }
}