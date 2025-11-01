package com.example.demo.service;

import com.example.demo.model.EventLog;
import com.example.demo.producer.StorelineKafkaProducer;
import com.example.demo.repository.EventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Handles incoming XML messages by publishing to Kafka and persisting in MongoDB.
 * Fully aligned with enterprise StoreLine implementation.
 */
@Service
@Lazy(false)
public class StoreLineService {

    private static final Logger log = LoggerFactory.getLogger(StoreLineService.class);

    @Autowired
    private StorelineKafkaProducer kafkaProducer;

    @Autowired
    private EventLogRepository eventLogRepository;

    public void initialize() {
        log.info("✅ StoreLineService initialized and ready to receive XML.");
    }

    public void processIncomingXml(String xml) {
        log.info("📥 Received XML...");
        log.debug("⚙️ Raw XML: {}", xml);
        log.info("📦 Processing XML message...");

        String uuid = UUID.randomUUID().toString();

        try {
            if (xml == null || xml.trim().isEmpty()) {
                throw new IllegalArgumentException("XML payload cannot be null or empty.");
            }

            String normalizedXml = xml.replaceAll("\\s+", " ");
            EventLog eventLog = new EventLog(uuid, normalizedXml, "storeline-main-topic", "PENDING");

            boolean mainTopicSuccess = false;
            boolean eventLogTopicSuccess = false;

            try {
                kafkaProducer.sendToKafka("storeline-main-topic", eventLog);
                mainTopicSuccess = true;
                log.info("✅ Sent to Kafka topic: storeline-main-topic");
            } catch (Exception e) {
                log.error("❌ Failed to send to Kafka topic: storeline-main-topic", e);
            }

            try {
                kafkaProducer.sendToKafka("storeline-eventlog-topic", eventLog);
                eventLogTopicSuccess = true;
                log.info("✅ Sent to Kafka topic: storeline-eventlog-topic");
            } catch (Exception e) {
                log.error("❌ Failed to send to Kafka topic: storeline-eventlog-topic", e);
            }

            if (mainTopicSuccess && eventLogTopicSuccess) {
                eventLog.setStatus("SENT");
            } else if (!mainTopicSuccess && !eventLogTopicSuccess) {
                eventLog.setStatus("FAILED_BOTH_TOPICS");
            } else if (!mainTopicSuccess) {
                eventLog.setStatus("FAILED_MAIN_TOPIC");
            } else {
                eventLog.setStatus("FAILED_EVENTLOG_TOPIC");
            }

            try {
                eventLogRepository.save(eventLog);
                log.info("💾 Stored event in MongoDB with UUID: {}", uuid);
            } catch (Exception e) {
                log.error("❌ Failed to store event in MongoDB for UUID: {}", uuid, e);
                eventLog.setStatus("FAILED_MONGODB");
            }

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid XML input: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error while processing XML with UUID: {}", uuid, e);
        }

        log.info("✅ XML processing completed for UUID: {}", uuid);
    }
}
