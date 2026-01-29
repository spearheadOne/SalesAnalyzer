package org.abondar.experimental.sales.analzyer.cleanup

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import org.abondar.experimental.sales.analyzer.cleanup.SalesCleanupHandler
import org.junit.jupiter.api.*
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SalesCleanupHandlerIT {

    private lateinit var applicationContext: ApplicationContext
    private lateinit var s3Client: S3Client
    private lateinit var salesBucket: String
    private lateinit var cleanupHandler: SalesCleanupHandler


    val cleanUpEvent = ScheduledEvent().apply {
        region = "us-east-1"
        source = "aws.events"
        detailType = "Scheduled Event"
    }


    companion object {
        @Container
        @JvmStatic
        val localstack: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
                .withServices("s3")
    }


    @BeforeEach
    fun setup() {
        val bucketName = "sales-bucket-${System.currentTimeMillis()}-${(1000..9999).random()}"

        val props = mutableMapOf<String, Any?>(
            "aws.services.s3.endpoint-override" to localstack.endpoint.toString(),
            "aws.access-key-id" to localstack.accessKey,
            "aws.secret-access-key" to localstack.secretKey,
            "aws.region" to localstack.region,
            "ingestion.bucket-name" to bucketName
        )

        applicationContext = ApplicationContext.run(PropertySource.of("test", props))

        salesBucket = applicationContext.getProperty("ingestion.bucket-name", String::class.java).get()
        s3Client = applicationContext.getBean(S3Client::class.java)
        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(salesBucket)
                .build()
        )

        cleanupHandler = SalesCleanupHandler(applicationContext)
    }

    @AfterEach
    fun tearDown() {
        applicationContext.close()
    }

    @Test
    fun `test empty bucket clean up`() {
        assertDoesNotThrow {
            cleanupHandler.execute(cleanUpEvent)
        }
    }

    @Test
    fun `test bucket clean up`() {
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

        assertDoesNotThrow {
            cleanupHandler.execute(cleanUpEvent)
        }
    }

}