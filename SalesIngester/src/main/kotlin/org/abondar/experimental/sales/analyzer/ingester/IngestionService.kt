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

                if (cols[0] == ColumnHeaders.TIMESTAMP.header) {
                    return@forEach
                }

                batch.add(
                    SalesRecord(
                        timestamp = Instant.parse(cols[ColumnHeaders.TIMESTAMP.index]),
                        productId = cols[ColumnHeaders.PRODUCT_ID.index],
                        productName = cols[ColumnHeaders.PRODUCT_NAME.index],
                        category = cols[ColumnHeaders.CATEGORY.index],
                        price = cols[ColumnHeaders.PRICE.index].toBigDecimal(),
                        currency = cols[ColumnHeaders.CURRENCY.index],
                        amount = cols[ColumnHeaders.AMOUNT.index].toInt(),
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