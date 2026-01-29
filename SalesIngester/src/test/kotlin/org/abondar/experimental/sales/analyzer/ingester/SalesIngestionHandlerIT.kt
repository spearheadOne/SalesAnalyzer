package org.abondar.experimental.sales.analyzer.ingester

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.serde.ObjectMapper
import org.abondar.experimental.sales.analyzer.ingester.input.SalesIngesterHandler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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


//normally we should use micronaut test resources but they do not support kinesis in localstack
//micronuat app context inits faster than test containers 2 so app context reqiures manual init
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SalesIngestionHandlerIT {

    private lateinit var applicationContext: ApplicationContext
    private lateinit var s3Client: S3Client
    private lateinit var objectMapper: ObjectMapper
    private lateinit var salesBucket: String
    private lateinit var region: String
    private lateinit var streamName: String

    companion object {
        @Container
        @JvmStatic
        val localstack: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
                .withServices("s3", "kinesis")

    }


    @BeforeEach
    fun setup() {
        localstack.start()

        val bucketName = "sales-bucket-${System.currentTimeMillis()}-${(1000..9999).random()}"


        val props = mutableMapOf<String, Any?>(
            "aws.services.s3.endpoint-override" to localstack.endpoint.toString(),
            "aws.services.kinesis.endpoint-override" to localstack.endpoint.toString(),
            "aws.access-key-id" to localstack.accessKey,
            "aws.secret-access-key" to localstack.secretKey,
            "aws.kinesis.stream" to "sales-stream",
            "aws.region" to localstack.region,
            "ingestion.bucket-name" to bucketName
        )

        applicationContext = ApplicationContext.run(PropertySource.of("test", props))

        streamName = applicationContext.getProperty("aws.kinesis.stream", String::class.java).get()
        val kinesisClient = applicationContext.getBean(KinesisAsyncClient::class.java)
        kinesisClient.createStream(
            CreateStreamRequest.builder()
                .streamName(streamName)
                .shardCount(1)
                .build()
        ).get()

        waitForStreamToBeActive(kinesisClient, streamName)

        salesBucket = applicationContext.getProperty("ingestion.bucket-name", String::class.java).get()
        s3Client = applicationContext.getBean(S3Client::class.java)
        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(salesBucket)
                .build()
        )

        region = applicationContext.getProperty("aws.region", String::class.java).get()
        objectMapper = applicationContext.getBean(ObjectMapper::class.java)
    }

    private fun waitForStreamToBeActive(kinesisClient: KinesisAsyncClient, streamName: String) {
        val maxAttempts = 30
        val delayMillis = 1000L

        repeat(maxAttempts) { attempt ->
            val response = kinesisClient.describeStream(
                DescribeStreamRequest.builder()
                    .streamName(streamName)
                    .build()
            ).get()

            val status = response.streamDescription().streamStatus()

            if (status == StreamStatus.ACTIVE) {
                return
            }

            if (attempt < maxAttempts - 1) {
                Thread.sleep(delayMillis)
            }
        }

        throw IllegalStateException("Stream $streamName did not become active within ${maxAttempts * delayMillis / 1000} seconds")
    }

    @AfterEach
    fun tearDown() {
        applicationContext.close()
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