package org.abondar.experimental.sales.analyzer.dashboard.stream

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggRow
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Singleton
class Feed() {
    private val sink = Sinks.many().multicast().onBackpressureBuffer<AggRow>()

    fun stream(): Flux<AggRow> = sink.asFlux()

    internal fun emit(aggRow: AggRow) = sink.tryEmitNext(aggRow)
}