package org.abondar.experimental.sales.analyzer.cleanup

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.function.aws.MicronautRequestHandler
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime

class SalesCleanupRuntime : AbstractMicronautLambdaRuntime<ScheduledEvent, Void?, ScheduledEvent, Void?>() {

    override fun createRequestHandler(vararg args: String?): MicronautRequestHandler<ScheduledEvent, Void?> {
        return SalesCleanupHandler()
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            SalesCleanupRuntime().run(*args)
        }
    }
}