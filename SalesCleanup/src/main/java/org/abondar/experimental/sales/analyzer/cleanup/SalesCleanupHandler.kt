package org.abondar.experimental.sales.analyzer.cleanup

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

class SalesCleanupHandler : MicronautRequestHandler<ScheduledEvent, Void> {

    private val log = LoggerFactory.getLogger(this::class.java)

    constructor() : super()

    //for tests
    constructor(ctx: ApplicationContext) : super(ctx)

    @Inject
    lateinit var cleanupService: SalesCleanupService


    override fun execute(input: ScheduledEvent?): Void? {
        log.info("Sales cleanup lambda invoked, event: {}", input)
        val cleanupSuccessFull = cleanupService.performCleanup()

        if (!cleanupSuccessFull) {
            throw IllegalStateException("Cleanup failed")
        }

        return null;
    }

}