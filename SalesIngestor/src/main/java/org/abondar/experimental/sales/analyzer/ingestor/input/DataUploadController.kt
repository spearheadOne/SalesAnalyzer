package org.abondar.experimental.sales.analyzer.ingestor.input

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Part
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import org.abondar.experimental.sales.analyzer.ingestor.IngestionService

@Requires(env = ["local"])
@Controller("/data")
class DataUploadController(
    private val ingestor: IngestionService
) {

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Post
    suspend fun uploadData(@Part data: CompletedFileUpload): HttpResponse<String> {

        data.inputStream.use { ingestor.ingestData(it) }

        return HttpResponse.ok("Uploaded")
    }

}