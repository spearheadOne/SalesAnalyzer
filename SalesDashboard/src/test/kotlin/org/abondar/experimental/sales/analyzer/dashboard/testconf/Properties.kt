package org.abondar.experimental.sales.analyzer.dashboard.testconf

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer

object Properties {

    fun micronaut() = mapOf(
        "micronaut.server.port" to -1,
        "endpoints.all.port" to -1,
        "micronaut.jms.sqs.enabled" to "true"
    )

    fun postgres(container: PostgreSQLContainer<*>) = mapOf(
        "datasources.default.url" to container.jdbcUrl,
        "datasources.default.username" to container.username,
        "datasources.default.password" to container.password,
        "datasources.default.driver-class-name" to "org.postgresql.Driver",
    )

    fun localstackAws(container: LocalStackContainer) = mapOf(
        "aws.region" to container.region,
        "aws.access-key-id" to container.accessKey,
        "aws.secret-access-key" to container.secretKey,
        "aws.services.sqs.endpoint-override" to container
            .getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
    )

}