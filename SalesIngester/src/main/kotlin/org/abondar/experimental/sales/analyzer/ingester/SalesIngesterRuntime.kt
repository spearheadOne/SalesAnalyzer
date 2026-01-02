package org.abondar.experimental.sales.analyzer.ingester

import com.amazonaws.services.lambda.runtime.RequestHandler
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime
import org.abondar.experimental.sales.analyzer.ingester.input.SalesIngesterHandler

class SalesIngesterRuntime :
    AbstractMicronautLambdaRuntime<String, Void?, String, Void?>() {

    override fun createRequestHandler(vararg args: String?): RequestHandler<String, Void?> {
        return SalesIngesterHandler()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SalesIngesterRuntime().run(*args)  // note the * to spread args
        }
    }
}