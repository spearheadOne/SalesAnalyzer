package org.abondar.experimental.sales.analyzer.fx

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.fx.rate.CurrencyConverter
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Singleton
class FxService(
    private val currencyConverter: CurrencyConverter
) : FxServiceGrpcKt.FxServiceCoroutineImplBase() {

    override suspend fun convertBatch(request: ConvertBatchRequest): ConvertBatchResponse {

        val results = request.itemsList.map { item ->
            val srcCurrency = Currency.getInstance(item.source.currencyCode)
            val srcAmount = BigDecimal(item.source.amount)
            val dstCurrency = Currency.getInstance(item.targetCurrencyCode)

            val asOf = Instant.ofEpochSecond(item.asOf.seconds, item.asOf.nanos.toLong())

            val convertedAmount = currencyConverter.convertAmount(srcCurrency, srcAmount, dstCurrency, asOf)

            ConvertResponseItem.newBuilder()
                .setProductId(item.productId)
                .setConverted(
                    Money.newBuilder()
                        .setCurrencyCode(dstCurrency.currencyCode)
                        .setAmount(convertedAmount.toString())
                        .build()
                )
                .build();
        }

        return ConvertBatchResponse.newBuilder()
            .addAllItems(results)
            .build()
    }


}