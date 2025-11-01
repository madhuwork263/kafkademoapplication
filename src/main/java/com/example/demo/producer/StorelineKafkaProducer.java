package com.example.demo.producer;

import com.example.demo.model.EventLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Handles Kafka publishing logic for StoreLine messages.
 * Eager initialization added for consistent startup readiness.
 */
@Component
@Lazy(false) // ✅ ensures producer initializes before first request
public class StorelineKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(StorelineKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send EventLog object to the specified Kafka topic.
     */
    public void sendToKafka(String topicName, EventLog eventLog) {
        try {
            kafkaTemplate.send(topicName, eventLog);
            log.info("📤 Sent message to Kafka topic: {} | Payload ID: {}", topicName, eventLog.getId());
        } catch (Exception e) {
            log.error("❌ Error sending message to Kafka topic: {}", topicName, e);
            throw e;
        }
    }
}
