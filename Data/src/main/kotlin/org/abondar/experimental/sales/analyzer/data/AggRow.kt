package org.abondar.experimental.sales.analyzer.data

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal
import java.time.Instant
import java.util.Currency

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
    val currency: Currency
)