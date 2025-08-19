package org.abondar.experimental.sales.analyzer.ingester

import kotlinx.coroutines.test.runTest
import org.abondar.exerimental.sales.analyzer.data.SalesRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class IngestionServiceTest {

    @InjectMocks
    lateinit var ingester: IngestionService

    @Mock
    lateinit var publisher: IngestionPublisher

    @Test
    fun `parses csv and publishes data`() = runTest {
        val data = """
            timestamp,order_id,customer_id,product_id,product_name,category,price,amount,currency,region
            2025-08-13T09:15:00Z,ORD10001,CUST001,PROD001,Wireless Mouse,Electronics,24.99,2,EUR,DE
            2025-08-13T09:16:30Z,ORD10002,CUST002,PROD002,USB-C Cable,Accessories,9.99,1,EUR,DE
            2025-08-13T09:18:10Z,ORD10003,CUST003,PROD003,Mechanical Keyboard,Electronics,89.50,1,EUR,FR
            2025-08-13T09:20:05Z,ORD10004,CUST004,PROD004,Laptop Stand,Office,34.90,1,EUR,FR
            2025-08-13T09:21:45Z,ORD10005,CUST005,PROD002,USB-C Cable,Accessories,9.99,3,EUR,DE
        """.trimIndent()

        ingester.ingestData(data.byteInputStream())

        val captor = argumentCaptor<List<SalesRecord>>()
        verify(publisher).publishMessage(captor.capture())

        val records = captor.firstValue
        assertEquals(5, records.size)
        assertEquals("ORD10001", records.first().orderId)
        assertEquals("CUST001", records.first().customerId)

    }

}