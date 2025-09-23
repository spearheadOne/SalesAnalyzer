package org.abondar.experimental.sales.analyzer.job.testconf

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


    @JvmField
    val LOCALSTACK: LocalStackContainer =
        LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
            .withServices( LocalStackContainer.Service.KINESIS, LocalStackContainer.Service.DYNAMODB,
                LocalStackContainer.Service.CLOUDWATCH, LocalStackContainer.Service.SQS)
}