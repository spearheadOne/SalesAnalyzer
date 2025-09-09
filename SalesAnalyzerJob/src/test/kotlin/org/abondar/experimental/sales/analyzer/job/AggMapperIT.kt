package org.abondar.experimental.sales.analyzer.job

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.time.Instant

@Testcontainers(disabledWithoutDocker = true)
class AggMapperIT {

    lateinit var aggMapper: AggMapper

    lateinit var applicationContext: ApplicationContext

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
    }

    @BeforeEach
    fun setup() {
        postgres.start()

        applicationContext = ApplicationContext.run(
            PropertySource.of(
                "test",
                mapOf(
                    "datasources.default.url" to postgres.jdbcUrl,
                    "datasources.default.username" to postgres.username,
                    "datasources.default.password" to postgres.password,
                    "datasources.default.driver-class-name" to "org.postgresql.Driver",
                    "liquibase.enabled" to "true",
                    "liquibase.datasources.default.change-log" to "classpath:db/changelog/db.changelog-master.yml"
                )
            )
        )

        aggMapper = applicationContext.getBean(AggMapper::class.java)
    }

    @AfterEach
    fun cleanup() {
        applicationContext.close()
    }

    @Test
    fun `test agg mapper save row`() {
        aggMapper.deleteAll()

        val agg = AggRow(
            Instant.now(), "test", "test", "test",
            1, 1, BigDecimal(10)
        )

        aggMapper.insertUpdateAgg(listOf(agg))

        val res = aggMapper.getAggregates()
        assertEquals(1, res.size)
        assertEquals(agg, res.first())
    }


}