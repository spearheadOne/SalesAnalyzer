package org.abondar.experimental.sales.analyzer.fx.rate

import jakarta.inject.Singleton
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap


//default currency is EUR. normally API used to fetch currency rates
@Singleton
class EurBasedCurrencyConverter : CurrencyConverter {

    private val ratesPerEur = ConcurrentHashMap(
        mapOf(
            Currency.getInstance("EUR") to BigDecimal(1),
            Currency.getInstance("USD") to BigDecimal(1.18),
            Currency.getInstance("GBP") to BigDecimal(1.35),
            Currency.getInstance("CHF") to BigDecimal(1.13),
            Currency.getInstance("JPY") to BigDecimal(130.27),
            Currency.getInstance("UAH") to BigDecimal(30.58),
            Currency.getInstance("RUB") to BigDecimal(90.50)
        )
    )


    override fun convertAmount(
        srcCurrency: Currency,
        srcAmount: BigDecimal,
        dstCurrency: Currency,
        asOf: Instant
    ): BigDecimal {
        val srcRate = ratesPerEur[srcCurrency] ?: throw IllegalStateException("Rate not found")
        val dstRate = ratesPerEur[dstCurrency] ?: throw IllegalStateException("Rate not found")

        return convert(srcAmount,srcRate,dstRate)
    }
}