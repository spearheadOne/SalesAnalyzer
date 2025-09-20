package org.abondar.experimental.sales.analyzer.job.testconf

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer

object Properties {

    fun postgres(container: PostgreSQLContainer<*>) = mapOf(
        "datasources.default.url" to container.jdbcUrl,
        "datasources.default.username" to container.username,
        "datasources.default.password" to container.password,
        "datasources.default.driver-class-name" to "org.postgresql.Driver",
        "liquibase.enabled" to "true",
        "liquibase.datasources.default.change-log" to "classpath:db/changelog/db.changelog-master.yml"
    )

    fun localstackAws(container: LocalStackContainer) = mapOf(
        "aws.region" to container.region,
        "aws.access-key-id" to container.accessKey,
        "aws.secret-access-key" to container.secretKey,
        "aws.services.kinesis.endpoint-override" to container
            .getEndpointOverride(LocalStackContainer.Service.KINESIS).toString(),
        "aws.services.dynamodb.endpoint-override" to container
            .getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString(),
        "aws.services.cloudwatch.endpoint-override" to container
            .getEndpointOverride(LocalStackContainer.Service.CLOUDWATCH).toString(),
        "aws.services.sqs.endpoint-override" to container
            .getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
    )

}