package org.abondar.experimental.sales.analyzer.job

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val ctx = ApplicationContext.builder()
            .environments(*resolveEnvs())
            .start()

        // close on JVM shutdown
        Runtime.getRuntime().addShutdownHook(Thread { ctx.close() })

        ctx.getBean(SalesAnalyzerJob::class.java).run()

    }

    private fun resolveEnvs(): Array<String> {
        val env = System.getenv("MICRONAUT_ENVIRONMENTS")
            ?: System.getProperty("micronaut.environments", "")
        return if (env.isNotBlank()) env.split(',').toTypedArray()
        else arrayOf(Environment.DEVELOPMENT)
    }
}