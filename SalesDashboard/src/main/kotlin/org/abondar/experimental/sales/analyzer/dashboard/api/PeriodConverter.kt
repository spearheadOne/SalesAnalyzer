package org.abondar.experimental.sales.analyzer.dashboard.api

import org.abondar.experimental.sales.analyzer.dashboard.exception.InvalidPeriodException

object PeriodConverter {

    fun toPeriod(period: String): String = when {
        period.matches(Regex("\\d+[mM]")) -> {
            val n = period.dropLast(1).toInt()
            "$n minutes"
        }

        period.matches(Regex("\\d+[hH]")) -> {
            val n = period.dropLast(1).toInt()
            "$n hours"
        }

        period.matches(Regex("\\d+[dD]")) -> {
            val n = period.dropLast(1).toInt()
            "$n days"
        }

        else -> throw InvalidPeriodException(period)
    }
}
