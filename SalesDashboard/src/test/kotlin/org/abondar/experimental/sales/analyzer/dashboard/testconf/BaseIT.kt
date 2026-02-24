package org.abondar.experimental.sales.analyzer.dashboard.testconf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.dashboard.data.SalesDashboardTestMapper
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.time.Instant

@Testcontainers(disabledWithoutDocker = true)
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseIT : TestPropertyProvider {

    @Inject
    protected lateinit var testMapper: SalesDashboardTestMapper

    companion object {

        val agg = AggRow(
            Instant.now(), "test", "test", "test",
            1, 1, BigDecimal(10),"EUR"
        )

        @Container
        @JvmField
        val POSTGRES: PostgreSQLContainer = PostgreSQLContainer(
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
        testMapper.deleteAll()
        testMapper.insertAgg(agg)
    }


    override fun getProperties(): Map<String, String> {
        val base = mutableMapOf<String, Any?>()
        base +=   mapOf(
            "datasources.default.url" to POSTGRES.jdbcUrl,
            "datasources.default.username" to POSTGRES.username,
            "datasources.default.password" to POSTGRES.password,
            "datasources.default.driver-class-name" to "org.postgresql.Driver",
            "micronaut.server.port" to -1,
            "endpoints.all.port" to -1,
            "micronaut.jms.sqs.enabled" to "true"
        )

        return base.mapValues { (_, v) -> v?.toString() ?: "" }
    }
}