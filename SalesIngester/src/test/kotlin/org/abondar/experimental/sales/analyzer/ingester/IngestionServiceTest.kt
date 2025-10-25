package org.abondar.experimental.sales.analyzer.ingester

import kotlinx.coroutines.test.runTest
import org.abondar.experimental.sales.analyzer.data.SalesRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import java.math.BigDecimal
import java.util.Currency

@ExtendWith(MockitoExtension::class)
class IngestionServiceTest {

    @InjectMocks
    lateinit var ingester: IngestionService

    @Mock
    lateinit var publisher: IngestionPublisher

    @Test
    fun `parses csv and publishes data`() = runTest {
        val data = """
            timestamp,product_id,product_name,category,price,currency,amount
            2025-08-13T09:15:00Z,PROD001,Wireless Mouse,Electronics,24.99,EUR,2
            2025-08-13T09:16:30Z,PROD002,USB-C Cable,Accessories,9.99,EUR,1
            2025-08-13T09:18:10Z,PROD003,Mechanical Keyboard,Electronics,89.50,EUR,2
            2025-08-13T09:20:05Z,PROD004,Laptop Stand,Office,34.90,EUR,5
            2025-08-13T09:21:45Z,PROD002,USB-C Cable,Accessories,9.99,EUR,3
        """.trimIndent()

        ingester.ingestData(data.byteInputStream())

        val captor = argumentCaptor<List<SalesRecord>>()
        verify(publisher).publishMessage(captor.capture())

        val records = captor.firstValue
        assertEquals(5, records.size)
        assertEquals("PROD001", records.first().productId)
        assertEquals("Wireless Mouse", records.first().productName)
        assertEquals("Electronics", records.first().category)
        assertEquals("24.99".toBigDecimal(), records.first().price)
        assertEquals(Currency.getInstance("EUR"), records.first().currency)
        assertEquals(2, records.first().amount)
    }

}