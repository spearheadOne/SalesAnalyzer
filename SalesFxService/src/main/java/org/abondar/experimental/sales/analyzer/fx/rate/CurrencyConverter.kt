package org.abondar.experimental.sales.analyzer.fx.rate

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.Instant
import java.util.*

interface CurrencyConverter {

    fun convertAmount(srcCurrency: Currency, srcAmount: BigDecimal, dstCurrency: Currency, asOf: Instant): BigDecimal


    fun convert(srcAmount: BigDecimal, srcRate: BigDecimal, dstRate: BigDecimal): BigDecimal {
        require(srcRate.signum() > 0 && dstRate.signum() > 0)

        if (srcRate == dstRate) return srcAmount

        val MC = MathContext.DECIMAL128
        val factor = dstRate.divide(srcRate,  MC)

        return srcAmount.multiply(factor,MC)
            .setScale(2, RoundingMode.HALF_UP)
    }
}