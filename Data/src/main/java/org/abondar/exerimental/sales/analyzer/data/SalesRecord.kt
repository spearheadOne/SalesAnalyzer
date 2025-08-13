package org.abondar.exerimental.sales.analyzer.data

import java.time.Instant

data class SalesRecord(
    val timestamp: Instant,
    val orderId: String,
    val customerId: String,
    val productId: String,
    val productName: String?,
    val category: String?,
    val price: Double,
    val amount: Int,
    val currency: String?,
    val region: String?
)
