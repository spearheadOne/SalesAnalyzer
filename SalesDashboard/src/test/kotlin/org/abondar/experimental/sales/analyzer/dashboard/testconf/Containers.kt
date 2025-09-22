package org.abondar.experimental.sales.analyzer.dashboard.testconf

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

object Containers {
    @JvmField
    val POSTGRES: PostgreSQLContainer<*> = PostgreSQLContainer(
        DockerImageName.parse("timescale/timescaledb:latest-pg14")
            .asCompatibleSubstituteFor("postgres")
    )
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true)
        .withInitScript("sql/init-db.sql")


    @JvmField
    val LOCALSTACK: LocalStackContainer =
        LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
            .withServices( LocalStackContainer.Service.SQS)
            .withReuse(true)

    init {
        if (!POSTGRES.isRunning) POSTGRES.start()
        if (!LOCALSTACK.isRunning) LOCALSTACK.start()
    }
}