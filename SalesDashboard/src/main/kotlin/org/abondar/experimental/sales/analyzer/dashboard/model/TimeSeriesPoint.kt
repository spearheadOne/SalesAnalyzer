package org.abondar.experimental.sales.analyzer.dashboard.model

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal
import java.time.Instant

@Introspected
@Serdeable
data class TimeSeriesPoint(
    val eventTime: Instant,
    val productId: String,
    val productName: String,
    val revenue: BigDecimal
)