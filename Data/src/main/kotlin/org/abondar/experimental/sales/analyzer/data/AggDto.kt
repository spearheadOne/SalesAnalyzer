package org.abondar.experimental.sales.analyzer.data

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant

@Serdeable
@Introspected
data class AggDto(
    val eventTime: Instant,
    val productId: String,
    val productName: String,
    val category: String,
    val orders: Long,
    val units: Long,
    val revenue: String,
    val currency: String,
    val origPrice: OrigPrice? = null
)


@Serdeable
@Introspected
data class OrigPrice(
    val price: String,
    val currency: String
)