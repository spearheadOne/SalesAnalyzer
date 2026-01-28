package org.abondar.experimental.sales.analyzer.dashboard.stream

import io.micronaut.jms.annotations.JMSListener
import io.micronaut.jms.annotations.Queue
import io.micronaut.jms.sqs.configuration.SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME
import io.micronaut.messaging.annotation.MessageBody
import io.micronaut.serde.ObjectMapper
import org.abondar.experimental.sales.analyzer.data.AggDto
import org.slf4j.LoggerFactory

@JMSListener(CONNECTION_FACTORY_BEAN_NAME)
class QueueListener(
    private val feed: Feed,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Queue("\${aws.sqs.queue-name}")
    fun receiveMessage(@MessageBody msg: String) {
        val aggDto = objectMapper.readValue(msg, AggDto::class.java)
        log.debug("Received message {}", aggDto)
        feed.emit(aggDto)
    }

}