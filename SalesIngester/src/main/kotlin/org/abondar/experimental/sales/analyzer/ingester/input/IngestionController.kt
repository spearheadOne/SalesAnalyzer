package org.abondar.experimental.sales.analyzer.ingester.input

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Part
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import org.abondar.experimental.sales.analyzer.ingester.IngestionService

@Requires(env = ["local"])
@Controller("/upload")
class IngestionController(
    private val ingestor: IngestionService
) {

    @Consumes(MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM, "text/csv")
    @Post("/{filename}")
    suspend fun uploadData(@PathVariable filename: String, @Body body: ByteArray): HttpResponse<String> {
        ingestor.ingestData(body.inputStream())
        return HttpResponse.ok("Uploaded")
    }

}