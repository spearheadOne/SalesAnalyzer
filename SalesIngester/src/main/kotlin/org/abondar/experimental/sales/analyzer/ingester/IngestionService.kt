package org.abondar.experimental.sales.analyzer.ingester

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.data.ColumnHeaders
import org.abondar.experimental.sales.analyzer.data.SalesRecord

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Instant

@Singleton
class IngestionService(
    private val publisher: IngestionPublisher
) {

    val batchSize = 500

    suspend fun ingestData(data: InputStream) {
        val batch = ArrayList<SalesRecord>()

        BufferedReader(InputStreamReader(data)).useLines { lines ->
            lines.forEach { line ->
                val cols = line.split(',')

                if (cols[0].equals(ColumnHeaders.TIMESTAMP.header)) {
                    return@forEach
                }


                //TODO: get rid of customerId (we don't use it)
                //TODO: get rid of region (we don't use it)
                batch.add(
                    SalesRecord(
                        timestamp = Instant.parse(cols[ColumnHeaders.TIMESTAMP.index]),
                        orderId = cols[ColumnHeaders.ORDER_ID.index],
                        customerId = cols[ColumnHeaders.CUSTOMER_ID.index],
                        productId = cols[ColumnHeaders.PRODUCT_ID.index],
                        productName = cols[ColumnHeaders.PRODUCT_NAME.index],
                        category = cols[ColumnHeaders.CATEGORY.index],
                        price = cols[ColumnHeaders.PRICE.index].toBigDecimal(),
                        amount = cols[ColumnHeaders.AMOUNT.index].toInt(),
                        currency = cols[ColumnHeaders.CURRENCY.index],
                        region = cols[ColumnHeaders.REGION.index]
                    )
                )
            }

            if (batch.size == batchSize) {
                publisher.publishMessage(batch)
                batch.clear()
            }

        }

        if (batch.isNotEmpty()) {
            publisher.publishMessage(batch)
        }
    }
}