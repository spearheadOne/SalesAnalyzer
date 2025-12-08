package org.abondar.experimental.sales.analzyer.cleanup

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.test.annotation.MicronautLambdaTest
import jakarta.inject.Inject
import org.abondar.experimental.sales.analyzer.cleanup.SalesCleanupHandler
import org.abondar.experimental.sales.analyzer.cleanup.SalesCleanupService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@MicronautLambdaTest
class SalesCleanupHandlerFailTest {
    @Inject
    lateinit var applicationContext: ApplicationContext

    @Test
    fun `handler fails`() {

        val failingService = mock<SalesCleanupService>()
        whenever(failingService.performCleanup()).thenReturn(false)

        val cleanUpEvent = ScheduledEvent().apply {
            region = "us-east-1"
            source = "aws.events"
            detailType = "Scheduled Event"
        }

        val handler = SalesCleanupHandler(applicationContext)
        handler.cleanupService = failingService

        assertThrows(IllegalStateException::class.java) {
            handler.execute(cleanUpEvent)
        }

    }
}