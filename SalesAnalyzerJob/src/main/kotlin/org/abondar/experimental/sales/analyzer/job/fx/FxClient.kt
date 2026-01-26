package org.abondar.experimental.sales.analyzer.job.fx

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Value
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton
import kotlinx.coroutines.withTimeout
import org.abondar.experimental.sales.analyzer.fx.ConvertBatchRequest
import org.abondar.experimental.sales.analyzer.fx.ConvertBatchResponse
import org.abondar.experimental.sales.analyzer.fx.FxServiceGrpcKt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Singleton
class FxClient(
    @param:GrpcChannel(SERVICE_NAME) private val channel: ManagedChannel,
    @param:Value("\${grpc.timeout-ms:}") private val timeout: Long
) {

    private val stub = FxServiceGrpcKt.FxServiceCoroutineStub(channel)

    suspend fun convertBatch(batchRequest: ConvertBatchRequest): ConvertBatchResponse =
        withTimeout(timeout.milliseconds) {
            stub.convertBatch(batchRequest)
        }


    companion object {
        const val SERVICE_NAME = "fx"
    }
}