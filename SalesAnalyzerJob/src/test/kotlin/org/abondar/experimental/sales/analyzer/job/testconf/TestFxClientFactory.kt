package org.abondar.experimental.sales.analyzer.job.testconf

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.abondar.experimental.sales.analyzer.fx.ConvertBatchRequest
import org.abondar.experimental.sales.analyzer.fx.ConvertBatchResponse
import org.abondar.experimental.sales.analyzer.fx.ConvertResponseItem
import org.abondar.experimental.sales.analyzer.fx.Money
import org.abondar.experimental.sales.analyzer.job.fx.FxClient
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.math.RoundingMode

@Factory
class TestFxClientFactory {

    @Singleton
    @Replaces(FxClient::class)
    fun fxClient(): FxClient {
        val mockClient = mock(FxClient::class.java)

        runBlocking {
            whenever(mockClient.convertBatch(any())).thenAnswer { inv ->
                val req = inv.getArgument<ConvertBatchRequest>(0)

                val items = req.itemsList.map { item ->
                    val rate = if (item.source.currencyCode == "USD" && item.targetCurrencyCode == "EUR") {
                        BigDecimal("0.90")
                    } else {
                        BigDecimal.ONE
                    }

                    val conv = item.source.amount.toBigDecimal().multiply(rate).setScale(2, RoundingMode.HALF_UP)

                    ConvertResponseItem.newBuilder()
                        .setCorrelationId(item.correlationId)
                        .setConverted(
                            Money.newBuilder()
                                .setCurrencyCode(item.targetCurrencyCode)
                                .setAmount(conv.toPlainString())
                        )
                        .build()
                }

                ConvertBatchResponse.newBuilder().addAllItems(items).build()
            }
        }

        return mockClient
    }
}
