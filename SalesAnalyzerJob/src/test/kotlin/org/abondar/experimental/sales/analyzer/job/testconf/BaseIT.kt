package org.abondar.experimental.sales.analyzer.job.testconf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import org.abondar.experimental.sales.analyzer.job.mapper.AggTestMapper

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers(disabledWithoutDocker = true)
open class BaseIT {
    protected lateinit var applicationContext: ApplicationContext

    protected lateinit var testMapper: AggTestMapper

    protected open fun extraProperties(): Map<String, Any?> = emptyMap()

    companion object {
        @Container
        @JvmField
        val POSTGRES: PostgreSQLContainer<*> = PostgreSQLContainer(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                .asCompatibleSubstituteFor("postgres")
        )
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")


        @Container
        @JvmField
        val LOCALSTACK: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
                .withServices( LocalStackContainer.Service.KINESIS, LocalStackContainer.Service.DYNAMODB,
                    LocalStackContainer.Service.CLOUDWATCH, LocalStackContainer.Service.SQS)
    }

    @BeforeEach
    fun start() {
        val props = mutableMapOf<String, Any?>()
        props += Properties.postgres(POSTGRES)
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