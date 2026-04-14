package org.abondar.experimental.sales.analyzer.cleanup

import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.Delete
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.ObjectIdentifier

@Singleton
class SalesCleanupService(
    private val s3Client: S3Client,
    @param:Value("\${ingestion.bucket-name}") private val bucketName: String
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun performCleanup(): Boolean {

        val objectsToClean = s3Client.listObjectsV2(
            ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build()
        )

        if (objectsToClean.keyCount() == 0) {
            log.info("No objects found in bucket {}", bucketName)
            return true
        }

        val identifiers = objectsToClean.contents().map { obj ->
            ObjectIdentifier.builder()
                .key(obj.key())
                .build()
        }

        val resp = s3Client.deleteObjects(
            DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(
                    Delete.builder()
                        .objects(identifiers)
                        .build()
                )
                .build()
        )

        if (resp.errors().count() > 0) {
            resp.errors().forEach { error ->
                log.error(
                    """
            Failed to delete S3 object:
              bucket  = {}
              key     = {}
              code    = {}
              message = {}
            """.trimIndent(),
                    bucketName,
                    error.key(),
                    error.code(),
                    error.message()
                )
            }
            return false
        } else {
            log.info("Successfully performed cleanup")
            return true
        }

    }

}