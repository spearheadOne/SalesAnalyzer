package org.abondar.experimental.sales.analyzer.ingester

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Value
import io.micronaut.function.aws.test.annotation.MicronautLambdaTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.ingester.input.SalesIngesterHandler
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest


//normally we should use micronaut test resources but they do not support kinesis in localstack
@Testcontainers(disabledWithoutDocker = true)
@MicronautLambdaTest
class SalesIngestionHandlerIT : TestPropertyProvider {

    @Inject
    lateinit var applicationContext: ApplicationContext

    @Value("\${ingestion.bucket-name}")
    lateinit var salesBucket: String

    @Value("\${aws.region}")
    lateinit var region: String

    @Value("\${aws.services.kinesis.stream}")
    lateinit var streamName: String

    @Inject
    lateinit var kinesisClient: KinesisAsyncClient

    @Inject
    lateinit var s3Client: S3Client

    companion object {
        @Container
        @JvmStatic
        val localstack: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
                .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.KINESIS)

    }

    override fun getProperties() = mutableMapOf(
        "aws.services.s3.endpoint-override" to localstack
            .getEndpointOverride(LocalStackContainer.Service.S3).toString(),
        "aws.services.kinesis.endpoint-override" to localstack
            .getEndpointOverride(LocalStackContainer.Service.KINESIS).toString(),
        "aws.access-key-id" to localstack.accessKey,
        "aws.secret-access-key" to localstack.secretKey,
        "aws.region" to localstack.region,
        "ingestion.bucket-name" to "sales-bucket"
    )

    @Test
    fun `test lambda handler processing s3 event`() {
        kinesisClient.createStream(
            CreateStreamRequest.builder()
                .streamName(streamName)
                .shardCount(1)
                .build()
        )

        salesBucket += "${System.currentTimeMillis()}-${(1000..9999).random()}"

        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(salesBucket)
                .build()
        )


        val data = """
            timestamp,product_id,product_name,category,price,currency,amount
            2025-08-13T09:15:00Z,PROD001,Wireless Mouse,Electronics,24.99,EUR,2
            2025-08-13T09:16:30Z,PROD002,USB-C Cable,Accessories,9.99,EUR,1
            2025-08-13T09:18:10Z,PROD003,Mechanical Keyboard,Electronics,89.50,EUR,2
            2025-08-13T09:20:05Z,PROD004,Laptop Stand,Office,34.90,EUR,5
            2025-08-13T09:21:45Z,PROD002,USB-C Cable,Accessories,9.99,EUR,3
        """.trimIndent()

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(salesBucket)
                .key("test.csv")
                .build(),
            RequestBody.fromString(data)
        )

        val s3Event = """
{
  "Records": [
    {
      "eventVersion": "2.1",
      "eventSource": "aws:s3",
      "awsRegion": "$region",
      "eventTime": "2025-08-15T12:00:00.000Z",
      "eventName": "ObjectCreated:Put",
      "s3": {
        "s3SchemaVersion": "1.0",
        "bucket": {
          "name": "$salesBucket",
          "ownerIdentity": { "principalId": "EXAMPLE" },
          "arn": "arn:aws:s3:::$salesBucket"
        },
        "object": {
          "key": "test.csv",
          "size": 1024,
          "eTag": "etag",
          "sequencer": "sequencer"
        }
      }
    }
  ]
}
""".trimIndent()

        val salesIngesterHandler = SalesIngesterHandler(applicationContext)
        assertDoesNotThrow { salesIngesterHandler.execute(s3Event) }
    }
}