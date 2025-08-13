package org.abondar.experimental.sales.analyzer.ingestor.input

import com.amazonaws.services.lambda.runtime.events.S3Event
import io.micronaut.context.annotation.Requires
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.ingestor.IngestionService

@Requires(env = ["aws"])
class DataUploadHandler : MicronautRequestHandler<S3Event, Void>() {

    @Inject
    lateinit var ingestor: IngestionService

    override fun execute(input: S3Event): Void? {
        TODO("Not yet implemented")
    }
}