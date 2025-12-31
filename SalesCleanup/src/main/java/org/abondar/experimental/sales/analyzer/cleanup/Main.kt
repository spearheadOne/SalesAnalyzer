package org.abondar.experimental.sales.analyzer.cleanup

import io.micronaut.context.ApplicationContext
import kotlin.system.exitProcess

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val ctx = ApplicationContext.builder().start()

        Runtime.getRuntime().addShutdownHook(Thread { ctx.close() })

        val cleanupSuccessFull = ctx.getBean(SalesCleanupService::class.java)
            .performCleanup()

        if (!cleanupSuccessFull)
            exitProcess(1)
        else
            exitProcess(0)
    }

}
