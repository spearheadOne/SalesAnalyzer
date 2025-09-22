package org.abondar.experimental.sales.analyzer.dashboard.stream

import io.micronaut.jms.annotations.JMSListener
import io.micronaut.jms.annotations.Queue
import io.micronaut.jms.sqs.configuration.SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME
import io.micronaut.messaging.annotation.MessageBody
import io.micronaut.serde.ObjectMapper
import org.abondar.experimental.sales.analyzer.data.AggRow
import org.slf4j.LoggerFactory

@JMSListener(CONNECTION_FACTORY_BEAN_NAME)
class QueueListener(
    private val feed: Feed,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Queue("\${aws.sqs.queueName}")
    fun receiveMessage(@MessageBody msg: String) {
        val aggRow = objectMapper.readValue(msg, AggRow::class.java)
        log.info("Received message $aggRow")
        feed.emit(aggRow)
    }

}