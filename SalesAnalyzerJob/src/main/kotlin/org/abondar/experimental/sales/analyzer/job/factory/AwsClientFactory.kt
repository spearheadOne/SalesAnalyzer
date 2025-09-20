package org.abondar.experimental.sales.analyzer.job.factory

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Factory
class AwsClientFactory(
    @param:Value("\${aws.region:us-east-1}") private val region: String,
    @param:Value("\${aws.access-key-id:}") private val accessKeyId: String?,
    @param:Value("\${aws.secret-access-key:}") private val secretAccessKey: String?,
    @param:Value("\${aws.services.kinesis.endpoint-override:}") private val kinesisEndpoint: String,
    @param:Value("\${aws.services.dynamodb.endpoint-override:}") private val dynamoDbEndpoint: String,
    @param:Value("\${aws.services.cloudwatch.endpoint-override:}") private val cloudwatchEndpoint: String,
    @param:Value("\${aws.services.sqs.endpoint-override:}") private val sqsEndpoint: String,
    ) {

    private var kinesisClient: KinesisAsyncClient? = null

    private var dynamoDbClient: DynamoDbAsyncClient? = null

    private var cloudWatchClient: CloudWatchAsyncClient? = null

    private var sqsAsyncClient: SqsAsyncClient? = null

    @Singleton
    @Requires(missingBeans = [KinesisAsyncClient::class])
    fun kinesisAsyncClient(): KinesisAsyncClient = KinesisAsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(resolveCredentialsProvider(kinesisEndpoint, accessKeyId, secretAccessKey))
            .apply {
                if (kinesisEndpoint.isNotBlank()) {
                    endpointOverride(URI.create(kinesisEndpoint))
                }
            }
            .build()
            .also { kinesisClient = it }

    @Singleton
    @Requires(missingBeans = [DynamoDbAsyncClient::class])
    fun dynamoDbAsyncClient(): DynamoDbAsyncClient = DynamoDbAsyncClient.builder()
        .region(Region.of(region))
        .credentialsProvider(resolveCredentialsProvider(dynamoDbEndpoint, accessKeyId, secretAccessKey))
        .apply {
            if (dynamoDbEndpoint.isNotBlank()) {
                endpointOverride(URI.create(dynamoDbEndpoint))
            }
        }
        .build()
        .also { dynamoDbClient = it }


    @Singleton
    @Requires(missingBeans = [CloudWatchAsyncClient::class])
    fun cloudwatchAsyncClient(): CloudWatchAsyncClient = CloudWatchAsyncClient.builder()
        .region(Region.of(region))
        .credentialsProvider(resolveCredentialsProvider(cloudwatchEndpoint, accessKeyId, secretAccessKey))
        .apply {
            if (cloudwatchEndpoint.isNotBlank()) {
                endpointOverride(URI.create(cloudwatchEndpoint))
            }
        }
        .build()
        .also { cloudWatchClient = it }


    @Singleton
    @Requires(missingBeans = [SqsAsyncClient::class])
    fun sqsAsyncClient(): SqsAsyncClient= SqsAsyncClient.builder()
        .region(Region.of(region))
        .credentialsProvider(resolveCredentialsProvider(sqsEndpoint, accessKeyId, secretAccessKey))
        .apply {
            if (sqsEndpoint.isNotBlank()) {
                endpointOverride(URI.create(sqsEndpoint))
            }
        }
        .build()
        .also { sqsAsyncClient = it }


    fun resolveCredentialsProvider(
        endpointOverride: String?,
        accessKeyId: String?,
        secretAccessKey: String?
    ): AwsCredentialsProvider {
        return if (!endpointOverride.isNullOrBlank()) {
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId ?: "test", secretAccessKey ?: "test")
            )
        } else {
            DefaultCredentialsProvider.builder().build()
        }
    }

    @PreDestroy
    fun close() {
        kinesisClient?.close()
        dynamoDbClient?.close()
        cloudWatchClient?.close()
        sqsAsyncClient?.close()
    }
}