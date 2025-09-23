package org.abondar.experimental.sales.analyzer.dashboard.testconf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import org.abondar.experimental.sales.analyzer.dashboard.data.SalesDashboardTestMapper
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
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

        @JvmField
        val POSTGRES: PostgreSQLContainer<*> = PostgreSQLContainer(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                .asCompatibleSubstituteFor("postgres")
        )
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("sql/init-db.sql")

    }

    @BeforeEach
    fun setup() {
        if (!POSTGRES.isRunning) POSTGRES.start()

        applicationContext = ApplicationContext.run(PropertySource.of("test",
            mapOf(
                "datasources.default.url" to POSTGRES.jdbcUrl,
                "datasources.default.username" to POSTGRES.username,
                "datasources.default.password" to POSTGRES.password,
                "datasources.default.driver-class-name" to "org.postgresql.Driver",
                "micronaut.server.port" to -1,
                "endpoints.all.port" to -1,
                "micronaut.jms.sqs.enabled" to "true"
            )))

        testMapper = applicationContext.getBean(SalesDashboardTestMapper::class.java)
        testMapper.deleteAll()
        testMapper.insertAgg(agg)
    }

    @AfterEach
    fun shutdown() {
        if (this::applicationContext.isInitialized) {
            applicationContext.close()
        }

        POSTGRES.stop()
    }
}