package org.abondar.experimental.sales.analyzer.cleanup

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject

class SalesCleanupHandler : MicronautRequestHandler<ScheduledEvent, Void> {

    constructor() : super()

    //for tests
    constructor(ctx: ApplicationContext) : super(ctx)

    @Inject
    lateinit var cleanupService: SalesCleanupService


    override fun execute(input: ScheduledEvent?): Void? {
        val cleanupSuccessFull = cleanupService.performCleanup()

        if (!cleanupSuccessFull) {
            throw IllegalStateException("Cleanup failed")
        }

        return null;
    }

}