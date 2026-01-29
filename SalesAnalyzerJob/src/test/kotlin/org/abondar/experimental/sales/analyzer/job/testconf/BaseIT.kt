package org.abondar.experimental.sales.analyzer.job.testconf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.PropertySource
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
open class BaseIT {
    protected lateinit var applicationContext: ApplicationContext

    protected lateinit var testMapper: AggTestMapper

    protected open fun extraProperties(): Map<String, Any?> = emptyMap()

    protected lateinit var fxClient: FxClient

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

    @BeforeEach
    fun start() {
        val props = mutableMapOf<String, Any?>()
        props += Properties.postgres(POSTGRES)
        props += extraProperties()

        applicationContext = ApplicationContext.run(PropertySource.of("test", props))

        testMapper = applicationContext.getBean(AggTestMapper::class.java)

        fxClient = mock(FxClient::class.java)

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

    @AfterEach
    fun shutdown() {
        if (this::applicationContext.isInitialized) {
            applicationContext.close()
        }
    }
}