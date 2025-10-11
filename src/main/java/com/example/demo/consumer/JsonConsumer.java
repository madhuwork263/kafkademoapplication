package com.example.demo.consumer;

import com.example.demo.model.JsonData;
import com.example.demo.service.JsonService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JsonConsumer {

    private final JsonService jsonService;

    public JsonConsumer(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    @KafkaListener(topics = "json_topic", groupId = "json_group",
            containerFactory = "jsonKafkaListenerContainerFactory")
    public void consume(JsonData data) {
        System.out.println("✅ Consumed JSON message: " + data);
        jsonService.saveData(data);
    }
}
