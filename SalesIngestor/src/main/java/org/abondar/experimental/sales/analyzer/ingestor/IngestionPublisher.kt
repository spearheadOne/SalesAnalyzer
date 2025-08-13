package org.abondar.experimental.sales.analyzer.ingestor

import jakarta.inject.Singleton
import org.abondar.exerimental.sales.analyzer.data.SalesRecord

@Singleton
class IngestionPublisher {

    suspend fun publishMessage(batch: List<SalesRecord>){

    }
}