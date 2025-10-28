package org.abondar.experimental.sales.analyzer.fx

import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Property
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

@MicronautTest
class FxServiceIT {

    @Inject
    @field:GrpcChannel(GrpcServerChannel.NAME)
    lateinit var channel: ManagedChannel

    @Test
    fun `convert Multiple currencies to EUR`() = runBlocking {
        val stub = FxServiceGrpcKt.FxServiceCoroutineStub(channel)

        val req = ConvertBatchRequest.newBuilder()
            .addItems(
                ConvertRequestItem.newBuilder()
                    .setSource(
                        Money.newBuilder()
                            .setCurrencyCode("USD")
                            .setAmount("100")
                            .build()
                    )
                    .setTargetCurrencyCode("EUR")
                    .setAsOf(
                        Timestamp.newBuilder()
                            .setSeconds(Instant.now().epochSecond)
                            .build()
                    )
                    .build()
            )
            .addItems(
                ConvertRequestItem.newBuilder()
                    .setSource(
                        Money.newBuilder()
                            .setCurrencyCode("GBP")
                            .setAmount("100")
                            .build()
                    )
                    .setTargetCurrencyCode("EUR")
                    .setAsOf(
                        Timestamp.newBuilder()
                            .setSeconds(Instant.now().epochSecond)
                            .build()
                    )
                    .build()
            )
            .build()

        val res = stub.convertBatch(req)
        res.itemsList.forEach {
            assertTrue(it.converted.amount.toDouble() > 0)
            assertEquals("EUR", it.converted.currencyCode)
        }

        assertEquals("84.75", res.itemsList.first().converted.amount)
        assertEquals("74.07", res.itemsList.last().converted.amount)

    }

}