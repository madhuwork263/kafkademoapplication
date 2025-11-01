package com.example.demo.producer;

import com.example.demo.model.CanonicalTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CanonicalKafkaProducer {

    private final KafkaTemplate<String, CanonicalTransaction> kafkaTemplate;

    @Value("${canonical.kafka.topic:canonical-topic}")
    private String topicName;

    public CanonicalKafkaProducer(KafkaTemplate<String, CanonicalTransaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCanonicalEvent(CanonicalTransaction transaction) {
        kafkaTemplate.send(topicName, transaction);
        System.out.println("📦 Sent canonical event to Kafka topic: " + topicName);
    }
}
