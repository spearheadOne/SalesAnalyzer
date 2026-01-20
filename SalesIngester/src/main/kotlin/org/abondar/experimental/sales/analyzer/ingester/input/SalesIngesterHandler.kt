package org.abondar.experimental.sales.analyzer.ingester.input

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Value
import io.micronaut.function.aws.MicronautRequestHandler
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.abondar.experimental.sales.analyzer.ingester.SalesIngestionService
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Serdeable
class SalesIngesterHandler : MicronautRequestHandler<S3EventNotification, Void> {

    //for prod
    constructor() : super()

    //for tests
    constructor(ctx: ApplicationContext) : super(ctx)

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${ingestion.bucket-name}")
    lateinit var salesBucket: String

    @Inject
    lateinit var ingester: SalesIngestionService

    @Inject
    lateinit var s3Client: S3Client

    override fun execute(s3Event: S3EventNotification): Void? {
        log.info("Sales ingester lambda invoked, event: {}", s3Event)

        s3Event.records
            .filter { it.s3.bucket.name == salesBucket }
            .forEach { record ->
                val bucket = record.s3.bucket.name
                val rawKey = record.s3.`object`.key
                val key =  URLDecoder.decode(rawKey, StandardCharsets.UTF_8)

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