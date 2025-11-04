package org.abondar.experimental.sales.analyzer.ingester

import jakarta.inject.Singleton
import org.abondar.experimental.sales.analyzer.ingester.ColumnHeaders
import org.abondar.experimental.sales.analyzer.data.SalesRecord

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Instant
import java.util.Currency

@Singleton
class IngestionService(
    private val publisher: IngestionPublisher
) {

    suspend fun ingestData(data: InputStream) {
        val batch = ArrayList<SalesRecord>()
        var bytesInBatch = 0
        val lastFlush = System.currentTimeMillis()

        BufferedReader(InputStreamReader(data)).useLines { lines ->
            lines.forEach { line ->
                if (line.isBlank()) return@forEach
                ColumnHeaders.TIMESTAMP.header?.let { if (line.startsWith(it)) return@forEach }

                val cols = line.split(',')

                batch.add(
                    SalesRecord(
                        timestamp = Instant.parse(cols[ColumnHeaders.TIMESTAMP.index]),
                        productId = cols[ColumnHeaders.PRODUCT_ID.index],
                        productName = cols[ColumnHeaders.PRODUCT_NAME.index],
                        category = cols[ColumnHeaders.CATEGORY.index],
                        price = cols[ColumnHeaders.PRICE.index].toBigDecimal(),
                        currency = cols[ColumnHeaders.CURRENCY.index].uppercase(),
                        amount = cols[ColumnHeaders.AMOUNT.index].toLong(),
                    )
                )

                bytesInBatch +=line.toByteArray().size
                val elapsedTime = System.currentTimeMillis() - lastFlush

                if (batch.size == MAX_BATCH_SIZE || bytesInBatch >= MAX_BYTES || elapsedTime >= MAX_ELAPSED_MS) {
                    publisher.publishMessage(batch)
                    batch.clear()
                }
            }
        }

        if (batch.isNotEmpty()) {
            publisher.publishMessage(batch)
        }
    }

    companion object {
        const val MAX_BATCH_SIZE = 500
        const val MAX_ELAPSED_MS = 200L
        const val MAX_BYTES = 4_500_000 // 4.5 mb
    }
}