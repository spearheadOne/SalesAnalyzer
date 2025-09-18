package org.abondar.experimental.sales.analyzer.dashboard.exception

class InvalidPeriodException(period: String) : RuntimeException("Invalid period $period")