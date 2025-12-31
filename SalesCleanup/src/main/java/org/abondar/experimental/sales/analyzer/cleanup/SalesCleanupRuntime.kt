package org.abondar.experimental.sales.analyzer.cleanup

import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime


class SalesCleanupRuntime :
    AbstractMicronautLambdaRuntime<ScheduledEvent, Void?, ScheduledEvent, Void?>() {

    override fun createRequestHandler(vararg args: String?): RequestHandler<ScheduledEvent, Void?> {
        return SalesCleanupHandler()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SalesCleanupRuntime().run(*args)
        }
    }
}