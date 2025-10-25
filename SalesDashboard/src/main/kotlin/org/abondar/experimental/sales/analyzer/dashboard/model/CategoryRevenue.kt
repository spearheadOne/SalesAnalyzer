package org.abondar.experimental.sales.analyzer.dashboard.model

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal


//TODO: retrieve currency from db
@Introspected
@Serdeable
data class CategoryRevenue(
    val category: String,
    val revenue: BigDecimal
)