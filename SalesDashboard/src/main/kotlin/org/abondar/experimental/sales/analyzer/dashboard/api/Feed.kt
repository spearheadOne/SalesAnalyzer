package org.abondar.experimental.sales.analyzer.dashboard.api

import io.micronaut.context.annotation.Value
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.dashboard.data.SalesDashboardMapper
import org.abondar.experimental.sales.analyzer.dashboard.model.TimeSeriesPoint
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

@Singleton
class Feed(
    private val mapper: SalesDashboardMapper,
    @param:Value("\${seen.poll-seconds:1}") private val pollSeconds: Long,
    @param:Value("\${seen.max-batch:100}") private val maxBatch: Int
) {
    private val cursor = AtomicReference(Cursor(Instant.now(), ""))

    fun stream(): Flux<TimeSeriesPoint> = Flux
        .interval(Duration.ofSeconds(pollSeconds.coerceIn(1, 5)))
        .onBackpressureDrop()
        .concatMap {
            Mono.fromCallable {
                val cursorVal = cursor.get()
                mapper.timeSeriesSince(cursorVal.timeStamp, cursorVal.lastProductId, maxBatch.toInt())
            }
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapIterable { rows ->
                    if (rows.isNotEmpty()) {
                        val last = rows.last()
                        cursor.set(Cursor(last.bucketStartTime, last.productId))
                    }
                    rows
                }
        }
        .retryWhen(
            reactor.util.retry.Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
                .maxBackoff(Duration.ofSeconds(30))
        )
        .publish()
        .refCount(1)
}

data class Cursor(val timeStamp: Instant, val lastProductId: String)
