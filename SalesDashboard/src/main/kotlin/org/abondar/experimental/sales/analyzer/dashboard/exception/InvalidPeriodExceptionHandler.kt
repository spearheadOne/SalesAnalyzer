package org.abondar.experimental.sales.analyzer.dashboard.exception

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Produces
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.hateoas.Link
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton


@Produces(MediaType.APPLICATION_JSON)
@Singleton
class InvalidPeriodExceptionHandler : ExceptionHandler<InvalidPeriodException, HttpResponse<JsonError>> {
    override fun handle(request: HttpRequest<*>, exception: InvalidPeriodException): HttpResponse<JsonError> {
        val error = JsonError(exception.message)
            .link(Link.SELF, Link.of(exception.message))

        return HttpResponse.badRequest(error)
    }
}