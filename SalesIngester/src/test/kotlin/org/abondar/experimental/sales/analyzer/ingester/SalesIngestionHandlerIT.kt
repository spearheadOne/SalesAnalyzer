package org.abondar.experimental.sales.analyzer.ingester

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.NonNull
import io.micronaut.function.aws.test.annotation.MicronautLambdaTest
import io.micronaut.serde.ObjectMapper
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.ingester.input.SalesIngesterHandler
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest
import software.amazon.awssdk.services.kinesis.model.StreamStatus
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException
import java.util.concurrent.TimeUnit


@Testcontainers(disabledWithoutDocker = true)
@MicronautLambdaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SalesIngestionHandlerIT : TestPropertyProvider {

    @Inject
    private lateinit var s3Client: S3Client

    @Inject
    private lateinit var objectMapper: ObjectMapper

    @Inject
    private lateinit var applicationContext: ApplicationContext

    @Value("\${ingestion.bucket-name}")
    lateinit var salesBucket: String

    @Value("\${aws.region}")
    lateinit var region: String

    @Inject
    lateinit var kinesisClient: KinesisAsyncClient

    private val streamName = "sales-stream-${System.currentTimeMillis()}-${(1000..9999).random()}"

    companion object {
        @Container
        @JvmStatic
        val localstack: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
                .withServices("s3", "kinesis")
                .waitingFor(Wait.forLogMessage(".*Ready.*", 1))

    }


    override fun getProperties(): @NonNull Map<String?, String?> {
        val bucketName = "sales-bucket-${System.currentTimeMillis()}-${(1000..9999).random()}"

       return  mutableMapOf<String?, String?>(
           "aws.services.s3.endpoint-override" to localstack.endpoint.toString(),
           "aws.services.kinesis.endpoint-override" to localstack.endpoint.toString(),
           "aws.access-key-id" to localstack.accessKey,
           "aws.secret-access-key" to localstack.secretKey,
           "aws.services.kinesis.stream" to streamName,
           "aws.s3.path-style-access-enabled" to "true",
           "aws.region" to localstack.region,
           "ingestion.bucket-name" to bucketName
       )
    }


    @BeforeEach
    fun setup() {
        kinesisClient.createStream(
            CreateStreamRequest.builder()
                .streamName(streamName)
                .shardCount(1)
                .build()
        ).get(10, TimeUnit.SECONDS)

        var streamActive = false
        var attempts = 0
        while (!streamActive && attempts < 30) {
            try {
                val describeResponse = kinesisClient.describeStream(
                    DescribeStreamRequest.builder()
                        .streamName(streamName)
                        .build()
                ).get(5, TimeUnit.SECONDS)

                streamActive = describeResponse.streamDescription().streamStatus() == StreamStatus.ACTIVE
            } catch (e: Exception) {
                if (e.cause !is ResourceNotFoundException) {
                    throw e
                }
            }

            if (!streamActive) {
                Thread.sleep(1000)
                attempts++
            }
        }

        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(salesBucket)
                .build()
        )


    }



    @Test
    fun `test lambda handler processing s3 event`() {
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

        val s3EventNotification: S3EventNotification = objectMapper
            .readValue(s3Event, S3EventNotification::class.java)

        val salesIngesterHandler = SalesIngesterHandler(applicationContext)
        assertDoesNotThrow { salesIngesterHandler.execute(s3EventNotification) }
    }
}