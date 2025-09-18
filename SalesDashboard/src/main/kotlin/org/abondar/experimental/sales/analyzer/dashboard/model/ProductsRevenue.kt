package org.abondar.experimental.sales.analyzer.dashboard.model

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Introspected
@Serdeable
data class ProductsRevenue(
    val productId: String,
    val productName: String,
    val revenue: BigDecimal,
    val orders: Long,
    val units: Long
)
