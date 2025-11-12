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
data class ProductsRevenueItemDto(
    val productId: String,
    val productName: String,
    val revenue: String,
    val orders: Long,
    val units: Long,
    val currency: String
)

@Introspected
@Serdeable
data class ProductRevenueDto(
    val defaultCurrency: String,
    val items: List<ProductsRevenueItemDto>
)


fun ProductsRevenue.toDto(currency: String): ProductsRevenueItemDto {
    return ProductsRevenueItemDto(productId, productName, revenue.toPlainString(), orders, units, currency)
}