package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.context.annotation.Value
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.abondar.experimental.sales.analyzer.dashboard.data.SalesDashboardMapper
import org.abondar.experimental.sales.analyzer.dashboard.model.CategoryRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.ProductsRevenue
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPoint
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

@Tag(name = "Sales dashboard", description = "Operations related to fetching various aggregated sales data")
@Validated
@Controller("/dashboard")
class SalesDashboardController(
    private val mapper: SalesDashboardMapper,
    @param:Value("\${seen.poll-seconds:1}") private val pollSeconds: Long,
) {

    @Operation(
        summary = "Fetch aggregated revenue data for a given time window",
        description = """
        Returns time series points from the sales aggregation table, grouped by product 
        and filtered to a relative time period. 
    """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Time series point data within period"),
            ApiResponse(responseCode = "400", description = "Incorrect period value")
        ]
    )
    @Get(uri = "/time-series/{period}", produces = [MediaType.APPLICATION_JSON])
    fun timeSeriesForPeriod(
        @Parameter(
            description = "Relative time window for selecting data. " +
                    "Use a number followed by a unit: 'm' = minutes, 'h' = hours, 'd' = days. " +
                    "Examples: '5m' → last 5 minutes, '2h' → last 2 hours, '7d' → last 7 days.",
            example = "5m",
            schema = Schema(type = "string", pattern = "\\d+[mMhHdD]")
        )
        @PathVariable @NotBlank period: String
    ): List<TimeSeriesPoint> {
        val dbPeriod = PeriodConverter.toPeriod(period)
        return mapper.timeSeriesPeriod(dbPeriod)
    }


    @Operation(
        summary = "Fetch top categories with revenue for a given time window",
        description = """
        Returns category and its revenue from the sales aggregation table, grouped by category 
        and filtered to a relative time period.
    """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Top categories within period"),
            ApiResponse(responseCode = "400", description = "Incorrect period value")
        ]
    )
    @Get(uri = "/categories/{period}", produces = [MediaType.APPLICATION_JSON])
    fun categoriesForPeriod(
        @Parameter(
            description = "Relative time window for selecting data. " +
                    "Use a number followed by a unit: 'm' = minutes, 'h' = hours, 'd' = days. " +
                    "Examples: '5m' → last 5 minutes, '2h' → last 2 hours, '7d' → last 7 days.",
            example = "5m",
            schema = Schema(type = "string", pattern = "\\d+[mMhHdD]")
        )
        @PathVariable @NotBlank period: String,
        @Min(1) @Max(100)

        @QueryValue(defaultValue = "10") limit: Int
    ): List<CategoryRevenue> {
        val dbPeriod = PeriodConverter.toPeriod(period)
        return mapper.topCategoriesPerPeriod(dbPeriod, limit)
    }

    @Operation(
        summary = "Fetch top products by revenue for a given time window",
        description = """
        Returns product from the sales aggregation table, ordered by revenue,
        and filtered to a relative time period.
    """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Top products by revenue within period"),
            ApiResponse(responseCode = "400", description = "Incorrect period value")
        ]
    )
    @Get(uri = "/products/{period}", produces = [MediaType.APPLICATION_JSON])
    fun productsByRevenue(
        @Parameter(
            description = "Relative time window for selecting data. " +
                    "Use a number followed by a unit: 'm' = minutes, 'h' = hours, 'd' = days. " +
                    "Examples: '5m' → last 5 minutes, '2h' → last 2 hours, '7d' → last 7 days.",
            example = "5m",
            schema = Schema(type = "string", pattern = "\\d+[mMhHdD]")
        )
        @PathVariable @NotBlank period: String,
        @Min(1) @Max(100)
        @QueryValue(defaultValue = "10") limit: Int
    ): List<ProductsRevenue> {
        val dbPeriod = PeriodConverter.toPeriod(period)
        return mapper.topProductsByRevenue(dbPeriod, limit)
    }

    @Operation(
        summary = "Fetch newly added data as it becomes available",
        description = """
        Returns time series points from the sales aggregation table.
    """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Recently added data")
        ]
    )
    @Get(uri = "/stream", produces = [MediaType.APPLICATION_JSON_STREAM])
    fun streamTimeSeries(): Flux<TimeSeriesPoint> {
        val seen = ConcurrentHashMap.newKeySet<Pair<Instant, String>>()
        val since = AtomicReference(Instant.EPOCH)

        return Flux.interval(Duration.ofSeconds(pollSeconds))
            .onBackpressureDrop()
            .flatMap { tickCount ->

                if (tickCount % 10 == 0L) {
                    seen.clear()
                }

                Mono.fromCallable {
                    mapper.timeSeriesSince(since.get())
                }
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMapMany { rows ->
                        since.updateAndGet { cur ->
                            rows.maxOfOrNull { it.bucketStartTime }?.takeIf { it.isAfter(cur) } ?: cur
                        }
                        Flux.fromIterable(
                            rows.filter { seen.add(it.bucketStartTime to it.productName) }
                        )
                    }
            }
    }
}
