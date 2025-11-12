package org.abondar.experimental.sales.analyzer.dashboard.model

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Introspected
data class TimeSeriesPoint(
    val eventTime: Instant,
    val productId: String,
    val productName: String,
    val revenue: BigDecimal
)

@Introspected
@Serdeable
data class TimeSeriesPointDto(
    val eventTime: Instant,
    val productId: String,
    val productName: String,
    val revenue: String,
    val currency: String
)

@Introspected
@Serdeable
data class TimeSeriesDto(
    val defaultCurrency: String,
    val points: List<TimeSeriesPointDto>
)


fun TimeSeriesPoint.toDto(currency: String): TimeSeriesPointDto {
    return TimeSeriesPointDto(eventTime, productId, productName, revenue.toPlainString(), currency)
}