package org.abondar.experimental.sales.analyzer.ingester

import io.micronaut.core.annotation.Introspected

@Introspected
enum class ColumnHeaders(val index: Int, val header: String? = null) {
    TIMESTAMP(0,"timestamp"),
    PRODUCT_ID(1, "product_id"),
    PRODUCT_NAME(2, "product_name"),
    CATEGORY(3, "category"),
    PRICE(4, "price"),
    CURRENCY(5, "currency"),
    AMOUNT(6, "amount")
}