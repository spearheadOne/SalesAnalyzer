package org.abondar.experimental.sales.analyzer.dashboard.model

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Introspected
data class ProductsRevenue(
    val productId: String,
    val productName: String,
    val revenue: BigDecimal,
    val orders: Long,
    val units: Long
)

@Introspected
@Serdeable
data class ProductsRevenueDto(
    val productId: String,
    val productName: String,
    val revenue: String,
    val orders: Long,
    val units: Long,
    val currency: String
)

fun ProductsRevenue.toDto(currency: String): ProductsRevenueDto {
    return ProductsRevenueDto(productId, productName, revenue.toPlainString(), orders, units, currency)
}