package org.abondar.experimental.sales.analyzer.job.testconf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import org.abondar.experimental.sales.analyzer.job.data.AggTestMapper
import org.abondar.experimental.sales.analyzer.job.testconf.Containers.LOCALSTACK
import org.abondar.experimental.sales.analyzer.job.testconf.Containers.POSTGRES
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
open class BaseIT {
    protected lateinit var applicationContext: ApplicationContext

    protected lateinit var testMapper: AggTestMapper

    protected open fun extraProperties(): Map<String, Any?> = emptyMap()

    @BeforeEach
    fun start() {
        val props = mutableMapOf<String, Any?>()
        props += Properties.postgres(Containers.POSTGRES)
        props += extraProperties()

        applicationContext = ApplicationContext.run(PropertySource.of("test", props))

        testMapper = applicationContext.getBean(AggTestMapper::class.java)
    }

    @AfterEach
    fun shutdown() {
        if (this::applicationContext.isInitialized) {
            applicationContext.close()
        }
    }
}