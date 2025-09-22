package org.abondar.experimental.sales.analyzer.dashboard.testconf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import org.abondar.experimental.sales.analyzer.dashboard.data.SalesDashboardTestMapper
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.Instant

@Testcontainers(disabledWithoutDocker = true)
open class BaseIT {

    protected lateinit var applicationContext: ApplicationContext

    protected lateinit var testMapper: SalesDashboardTestMapper

    protected open fun extraProperties(): Map<String, Any?> = emptyMap()

    companion object {

        val agg = AggRow(
            Instant.now(), "test", "test", "test",
            1, 1, BigDecimal(10)
        )
    }

    @BeforeEach
    fun setup() {
        val props = mutableMapOf<String, Any?>()
        props += Properties.micronaut()
        props += Properties.postgres(Containers.POSTGRES)
        props += extraProperties()

        applicationContext = ApplicationContext.run(PropertySource.of("test", props))

        testMapper = applicationContext.getBean(SalesDashboardTestMapper::class.java)
        testMapper.deleteAll()
        testMapper.insertAgg(agg)
    }

    @AfterEach
    fun shutdown() {
        if (this::applicationContext.isInitialized) {
            applicationContext.close()
        }
    }
}