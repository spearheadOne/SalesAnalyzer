package org.abondar.experimental.sales.analyzer.job

import io.micronaut.context.annotation.Value
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.job.factory.SalesRecordProcessorFactory
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


@Singleton
class SalesAnalyzerJob(
    private val salesRecordFactory: SalesRecordProcessorFactory,
    private val kinesisClient: KinesisAsyncClient,
    private val dynamoDbClient: DynamoDbAsyncClient,
    private val cloudWatchClient: CloudWatchAsyncClient,
    @param:Value("\${aws.services.kinesis.stream:}") private val streamName: String,
    @param:Value("\${micronaut.application.name:}") private val appName: String
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Volatile private var scheduler: Scheduler? = null
    @Volatile private var runner: Future<*>? = null
    private val exec = Executors.newSingleThreadExecutor()

    fun run() {
        if (scheduler != null) return

        val workerId = "worker-${System.currentTimeMillis()}"
        val config = ConfigsBuilder(
            streamName,
            appName,
            kinesisClient,
            dynamoDbClient,
            cloudWatchClient,
            workerId,
            salesRecordFactory
        )

        scheduler = Scheduler(
            config.checkpointConfig(),
            config.coordinatorConfig(),
            config.leaseManagementConfig(),
            config.lifecycleConfig(),
            config.metricsConfig(),
            config.processorConfig(),
            config.retrievalConfig()
        )

        runner = exec.submit {
            log.info("SalesAnalyzerJob started")

            try {
                scheduler!!.run()
            } catch (e: Throwable) {
                log.error("SalesAnalyzerJob failed", e)
                throw e
            }

        }
        
    }
    fun stop() {
        scheduler?.startGracefulShutdown()
        try {
            runner?.get(5, TimeUnit.SECONDS)
        } catch (_: Exception) {}
        scheduler = null
        runner = null
    }

    @PreDestroy
    fun onShutdown() {
        stop()
        exec.shutdown()
    }
}