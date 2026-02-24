package org.abondar.experimental.sales.analyzer.job.testconf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.abondar.experimental.sales.analyzer.fx.ConvertBatchRequest
import org.abondar.experimental.sales.analyzer.fx.ConvertBatchResponse
import org.abondar.experimental.sales.analyzer.fx.ConvertResponseItem
import org.abondar.experimental.sales.analyzer.fx.Money
import org.abondar.experimental.sales.analyzer.job.fx.FxClient
import org.abondar.experimental.sales.analyzer.job.mapper.AggTestMapper

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever


import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.localstack.LocalStackContainer
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.time.Duration.Companion.milliseconds

@Testcontainers(disabledWithoutDocker = true)
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseIT: TestPropertyProvider {

    @Inject
    protected lateinit var testMapper: AggTestMapper

    @Inject
    protected lateinit var fxClient: FxClient

    protected open fun extraProperties(): Map<String, Any?> = emptyMap()

    companion object {
        @Container
        @JvmField
        val POSTGRES: PostgreSQLContainer = PostgreSQLContainer(
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
                .withServices( "kinesis", "dynamodb", "cloudwatch", "sqs")
    }

    override fun getProperties(): Map<String, String> {
        val base = mutableMapOf<String, Any?>()
        base += Properties.postgres(POSTGRES)
        base += Properties.localstackAws(LOCALSTACK)
        base += extraProperties()

        return base.mapValues { (_, v) -> v?.toString() ?: "" }
    }

    @BeforeEach
    fun start() {
        testMapper.deleteAll()

        runBlocking {
            withTimeout(100.milliseconds) {
                whenever(fxClient.convertBatch(any())).thenAnswer { inv ->
                    val req = inv.getArgument<ConvertBatchRequest>(0)

                    val items = req.itemsList.map {
                        val rate = if (it.source.currencyCode == "USD" && it.targetCurrencyCode == "EUR")
                            BigDecimal("0.90") else BigDecimal.ONE

                        val conv = it.source.amount.toBigDecimal().multiply(rate).setScale(2, RoundingMode.HALF_UP)

                        ConvertResponseItem.newBuilder()
                            .setCorrelationId(it.correlationId)
                            .setConverted(
                                Money.newBuilder()
                                    .setCurrencyCode(it.targetCurrencyCode)
                                    .setAmount(conv.toPlainString())
                            )
                            .build()
                    }

                    ConvertBatchResponse.newBuilder().addAllItems(items).build()
                }
            }
        }
    }

}