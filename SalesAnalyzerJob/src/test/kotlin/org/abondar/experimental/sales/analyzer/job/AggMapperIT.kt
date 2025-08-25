package org.abondar.experimental.sales.analyzer.job

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.time.Instant

@MicronautTest
@Testcontainers
class AggMapperIT : TestPropertyProvider {

    @Inject
    lateinit var aggMapper: AggMapper

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

    override fun getProperties() = mutableMapOf(
        "datasources.default.url" to postgres.jdbcUrl,
        "datasources.default.username" to postgres.username,
        "datasources.default.password" to postgres.password,
        "datasources.default.driver" to "org.testcontainers.jdbc.ContainerDatabaseDriver ",
    )

    @Test
    fun `test agg mapper save row`() {
        aggMapper.deleteAll()

        val agg = AggRow(
            Instant.now(), "test", "test",
            1, 1, BigDecimal(10)
        )

        aggMapper.insertUpdateAgg(agg)

        val res = aggMapper.getAggByProduct()
        assertEquals(1,res.size)
        assertEquals(agg,res.first())
    }


}