package org.abondar.experimental.sales.analyzer.dashboard

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import org.abondar.experimental.sales.analyzer.dashboard.data.SalesDashboardTestMapper
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.time.Instant

@Testcontainers(disabledWithoutDocker = true)
open class BaseIT {

    protected lateinit var applicationContext: ApplicationContext

    protected  lateinit var testMapper: SalesDashboardTestMapper

    companion object {

        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                .asCompatibleSubstituteFor("postgres")
        )
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("sql/init-db.sql")


        val agg = AggRow(
            Instant.now(), "test", "test", "test",
            1, 1, BigDecimal(10)
        )
    }

    @BeforeEach
    fun setup() {
        postgres.start()

        applicationContext = ApplicationContext.run(
            PropertySource.of("test", mapOf(
                "datasources.default.url" to postgres.jdbcUrl,
                "datasources.default.username" to postgres.username,
                "datasources.default.password" to postgres.password,
                "datasources.default.driver-class-name" to "org.postgresql.Driver",
                "micronaut.server.port" to -1,
                "endpoints.all.port" to -1,
            ))
        )
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