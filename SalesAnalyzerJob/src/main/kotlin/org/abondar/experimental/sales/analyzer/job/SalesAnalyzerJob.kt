package org.abondar.experimental.sales.analyzer.job

import io.micronaut.context.annotation.Value
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.abondar.experimental.sales.analyzer.data.SalesRecord
import org.abondar.experimental.sales.analyzer.job.sink.SalesAggSink
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kinesis.source.KinesisStreamsSource
import org.apache.flink.connector.kinesis.source.config.KinesisSourceConfigOptions
import org.apache.flink.connector.kinesis.source.enumerator.assigner.ShardAssignerFactory
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant


@Singleton
class SalesAnalyzerJob(
    private val mapper: ObjectMapper,
    @param:Value("\${aws.kinesis.stream}") private val streamName: String,
    @param:Value("\${aws.region}") private val region: String,
    @param:Value("\${aws.stream.windows.size-minutes}") private val windowSize: Int
) {

    fun run() {
        val accountId = System.getenv("AWS_ACCOUNT_ID") ?: "000000000000"
        val streamArn = "arn:aws:kinesis:$region:$accountId:stream/$streamName"

        val sourceConfig = Configuration().apply {
            set(
                KinesisSourceConfigOptions.STREAM_INITIAL_POSITION,
                KinesisSourceConfigOptions.InitialPosition.LATEST
            )

            set(KinesisSourceConfigOptions.SHARD_DISCOVERY_INTERVAL, Duration.ofMinutes(1))
        }

        val kdsSource: KinesisStreamsSource<String> = KinesisStreamsSource.builder<String>()
            .setStreamArn(streamArn)
            .setSourceConfig(sourceConfig)
            .setDeserializationSchema(SimpleStringSchema())
            .setKinesisShardAssigner(ShardAssignerFactory.uniformShardAssigner())
            .build()

        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        //raw string stream
        env.fromSource(
            kdsSource,
            WatermarkStrategy.noWatermarks(), streamName
        )
            .returns(TypeInformation.of(String::class.java))
            .uid("kinesis-source:${streamName}")
            //map to sales record stream
            .map { line ->
                mapper.readValue(line, SalesRecord::class.java)
            }
            .name("SalesRecordMapper")
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forBoundedOutOfOrderness<SalesRecord>(Duration.ofMinutes(2))
                    .withTimestampAssigner { e, _ -> e.timestamp.toEpochMilli() }
                    .withIdleness(Duration.ofSeconds(30))

            )
            //group by customer and product id and make window aggregate
            .keyBy { "${it.customerId}-${it.productId}" }
            //group keyed records into fixed chunks of windowSize minutes, based on event time
            .window(TumblingEventTimeWindows.of(Duration.ofMinutes(windowSize.toLong())))
            .process(object : ProcessWindowFunction<SalesRecord, AggRow, String, TimeWindow>() {
                override fun process(
                    key: String?,
                    context: Context,
                    elements: MutableIterable<SalesRecord>,
                    out: Collector<AggRow>
                ) {
                    var orders = 0L
                    var units = 0L
                    var revenue = BigDecimal.ZERO
                    var productId = ""
                    var productName = ""

                    var category = ""

                    for (e in elements) {
                        orders += 1
                        units += e.amount.toLong()
                        revenue = revenue.add(e.price.multiply(BigDecimal(e.amount)))
                        productName = e.productName
                        productId = e.productId
                        category = e.category
                    }

                    val bucketStartTime = Instant.ofEpochMilli(context.window().start)
                    out.collect(
                        AggRow(
                            bucketStartTime,
                            productId,
                            productName,
                            category,
                            orders,
                            units,
                            revenue
                        )
                    )
                }
            })
            .sinkTo(SalesAggSink())
            .name("SalesAggSink")

        env.enableCheckpointing(60000)
        env.execute("SalesAnalyzerJob")


    }
}