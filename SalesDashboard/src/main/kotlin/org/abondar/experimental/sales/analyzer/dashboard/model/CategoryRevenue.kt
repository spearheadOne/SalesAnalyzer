package org.abondar.experimental.sales.analyzer.dashboard.model

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal


@Introspected
data class CategoryRevenue(
    val category: String,
    val revenue: BigDecimal
)

@Introspected
@Serdeable
data class CategoryRevenueItemDto(
    val category: String,
    val revenue: String,
    val currency: String
)

@Introspected
@Serdeable
data class CategoryRevenueDto(
    val defaultCurrency: String,
    val items: List<CategoryRevenueItemDto>
)


fun CategoryRevenue.toDto(currency: String): CategoryRevenueItemDto {
    return CategoryRevenueItemDto(category, revenue.toPlainString(), currency)
}