package org.abondar.experimental.sales.analyzer.job.testconf

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Replaces
import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.job.fx.FxClient

@Factory
class TestMocks {
    @Singleton
    @Primary
    @Replaces(FxClient::class)
    fun fxClientMock(): FxClient = org.mockito.Mockito.mock(FxClient::class.java)
}