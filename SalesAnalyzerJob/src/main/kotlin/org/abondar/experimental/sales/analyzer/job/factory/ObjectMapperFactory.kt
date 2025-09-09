package org.abondar.experimental.sales.analyzer.job.factory

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class ObjectMapperFactory {

    @Singleton
    fun objectMapper(): ObjectMapper =
        ObjectMapper()
            .registerModule(JavaTimeModule())
            .findAndRegisterModules()
            .apply {
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            }

}