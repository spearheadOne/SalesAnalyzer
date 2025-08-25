package org.abondar.experimental.sales.analyzer.data

enum class ColumnHeaders(val index: Int, val header: String? = null) {
    TIMESTAMP(0,"timestamp"),
    ORDER_ID(1, "order_id"),
    CUSTOMER_ID(2, "customer_id"),
    PRODUCT_ID(3, "product_id"),
    PRODUCT_NAME(4, "product_name"),
    CATEGORY(5, "category"),
    PRICE(6, "price"),
    AMOUNT(7, "amount"),
    CURRENCY(8, "currency"),
    REGION(9, "region")
}