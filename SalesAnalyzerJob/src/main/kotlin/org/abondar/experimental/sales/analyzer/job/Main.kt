package org.abondar.experimental.sales.analyzer.job

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import org.abondar.experimental.sales.analyzer.job.data.AggMapper
import org.abondar.experimental.sales.analyzer.job.sink.SalesAggSinkMapperHolder

object Main {
    @JvmStatic
    fun main(args: Array<String>) {


        val ctx = ApplicationContext.builder()
            .environments(*resolveEnvs())
            .start()

        // close on JVM shutdown
        Runtime.getRuntime().addShutdownHook(Thread { ctx.close() })

        val aggMapper = ctx.getBean(AggMapper::class.java)
        SalesAggSinkMapperHolder.mapper = aggMapper

        ctx.getBean(SalesAnalyzerJob::class.java).run()

    }

    private fun resolveEnvs(): Array<String> {
        val env = System.getenv("MICRONAUT_ENVIRONMENTS")
            ?: System.getProperty("micronaut.environments", "")
        return if (env.isNotBlank()) env.split(',').toTypedArray()
        else arrayOf(Environment.DEVELOPMENT)
    }
}