package org.abondar.experimental.sales.analyzer.fx.rate

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.Currency

class CurrencyConverterTest {

    @Test
    fun `convert usd to eur`(){
        val rateProvider = EurBasedCurrencyConverter()
        val rate = rateProvider.convertAmount(Currency.getInstance("USD"), BigDecimal(100),
            Currency.getInstance("EUR"), Instant.now())

       assertEquals(BigDecimal.valueOf(84.75),rate)
    }

    @Test
    fun `convert eur to eur`(){
        val rateProvider = EurBasedCurrencyConverter()
        val rate = rateProvider.convertAmount(Currency.getInstance("EUR"), BigDecimal(100),
            Currency.getInstance("EUR"), Instant.now())

        assertEquals(BigDecimal.valueOf(100),rate)
    }
}