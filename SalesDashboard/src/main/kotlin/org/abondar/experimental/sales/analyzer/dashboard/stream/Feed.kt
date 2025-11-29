package org.abondar.experimental.sales.analyzer.dashboard.stream

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Duration

@Singleton
class Feed() {
    private val sink = Sinks.many().replay().limit<AggDto>(100)

    fun stream(): Flux<AggDto> = sink.asFlux()

    internal fun emit(aggDto: AggDto) = sink.tryEmitNext(aggDto)
}