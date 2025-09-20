package org.abondar.experimental.sales.analyzer.dashboard.api

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
    info = Info(
        title = "Sales Dashboard",
        description = "Api to provide real time dashboard for sales alongside with historical data",
        version = "1.0",
        license = License(name = "MIT")
    )
)
object Api