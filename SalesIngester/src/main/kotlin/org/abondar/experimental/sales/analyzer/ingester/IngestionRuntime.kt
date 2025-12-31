package org.abondar.experimental.sales.analyzer.ingester

import com.amazonaws.services.lambda.runtime.RequestHandler
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime
import org.abondar.experimental.sales.analyzer.ingester.input.IngestionHandler

class IngestionRuntime :
    AbstractMicronautLambdaRuntime<String, Void?, String, Void?>() {

    override fun createRequestHandler(vararg args: String?): RequestHandler<String, Void?> {
        return IngestionHandler()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            IngestionRuntime().run(*args)  // note the * to spread args
        }
    }
}