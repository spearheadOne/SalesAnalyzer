package org.abondar.exerimental.sales.analyzer.data

data class AggRow(
    val bucketStartSeconds: Long,
    val productId: String,
    val category: String,
    val orders: Long,
    val units: Long,
    val revenue: Double
)