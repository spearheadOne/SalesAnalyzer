package org.abondar.experimental.sales.analyzer.data

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal
import java.time.Instant

@Serdeable
@Introspected
data class AggRow(
    val eventTime: Instant,
    val productId: String,
    val productName: String,
    val category: String,
    val orders: Long,
    val units: Long,
    val revenue: BigDecimal,
    val currency: String
) {
    companion object {
        fun builder() = Builder()

        inline fun build(init: Builder.() -> Unit): AggRow =
            Builder().apply(init).build()
    }

    class Builder {
        lateinit var eventTime: Instant
        lateinit var productId: String
        lateinit var productName: String
        lateinit var category: String
        var orders: Long = 1
        var units: Long = 0
        lateinit var revenue: BigDecimal
        lateinit var currency: String

        fun build(): AggRow = AggRow(
            eventTime, productId, productName, category,
            orders, units, revenue, currency
        )
    }
}