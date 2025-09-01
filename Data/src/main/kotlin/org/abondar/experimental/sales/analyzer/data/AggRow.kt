package org.abondar.experimental.sales.analyzer.data

import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import java.time.Instant

@Introspected
data class AggRow(
    val bucketStartTime: Instant,
    val productId: String,
    val productName: String,
    val category: String,
    val orders: Long,
    val units: Long,
    val revenue: BigDecimal
)