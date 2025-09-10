package org.abondar.experimental.sales.analyzer.dashboard.model

import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal

@Introspected
data class ProductsRevenue(
    val productId: String,
    val productName: String,
    val revenue: BigDecimal,
    val orders: Long,
    val units: Long
)
