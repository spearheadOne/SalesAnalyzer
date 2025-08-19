package org.abondar.experimental.sales.analyzer.ingester.input

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Value
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.abondar.experimental.sales.analyzer.ingester.IngestionService
import org.slf4j.LoggerFactory
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest

class IngestionHandler : MicronautRequestHandler<String, Void> {

    //for prod
    constructor() : super()

    //for tests
    constructor(ctx: ApplicationContext) : super(ctx)

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${ingestion.bucket-name}")
    lateinit var salesBucket: String

    @Inject
    lateinit var ingester: IngestionService

    @Inject
    lateinit var s3Client: S3Client

    override fun execute(input: String): Void? {
        val s3Event = S3EventNotification
            .fromJson(input)

        s3Event.records
            .filter { it.s3.bucket.name == salesBucket }
            .forEach { record ->
                val bucket = record.s3.bucket.name
                val key = record.s3.`object`.key

                log.info("Processing $key from bucket $bucket")

                val req = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build()


                s3Client.getObject(req).use { inputStream ->
                    runBlocking {
                        ingester.ingestData(inputStream)
                    }
                }

            }

        return null
    }
}