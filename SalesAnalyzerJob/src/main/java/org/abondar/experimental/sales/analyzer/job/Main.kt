package org.abondar.experimental.sales.analyzer.job

import io.micronaut.context.ApplicationContext

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        ApplicationContext.run().use { ctx ->
            println("Starting Flink Job")
          //  ctx.getBean(FlinkJob::class.java).run()
        }
    }
}