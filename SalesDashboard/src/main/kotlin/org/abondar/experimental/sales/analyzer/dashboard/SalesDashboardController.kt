package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.context.annotation.Value
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.validation.Validated
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

@Validated
@Controller("/dashboard")
class SalesDashboardController(
    private val mapper: SalesDashboardMapper,
    @param:Value("\${seen.poll-seconds:1}") private val pollSeconds: Long,
) {

    @Get(uri = "/time-series/{period}", produces = [MediaType.APPLICATION_JSON])
    fun timeSeriesForPeriod(@PathVariable @NotBlank period: String): List<TimeSeriesPoint> {
        val dbPeriod = PeriodConverter.toPeriod(period)
        return mapper.timeSeriesPeriod(dbPeriod)
    }


    @Get(uri = "/categories/{period}", produces = [MediaType.APPLICATION_JSON])
    fun categoriesForPeriod(
        @PathVariable @NotBlank period: String,
        @Min(1) @Max(100)
        @QueryValue(defaultValue = "10") limit: Int
    ): List<CategoryRevenue> {
        val dbPeriod = PeriodConverter.toPeriod(period)
        return mapper.topCategoriesPerPeriod(dbPeriod, limit)
    }

    @Get(uri = "/products/{period}", produces = [MediaType.APPLICATION_JSON])
    fun productsByRevenue(
        @PathVariable @NotBlank period: String,
        @Min(1) @Max(100)
        @QueryValue(defaultValue = "10") limit: Int
    ): List<ProductsRevenue> {
        val dbPeriod = PeriodConverter.toPeriod(period)
        return mapper.topProductsByRevenue(dbPeriod, limit)
    }

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
