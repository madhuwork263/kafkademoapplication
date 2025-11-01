package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "event_logs")
public class EventLog {

    @Id
    private String id;
    private String payload;
    private Instant timestamp;
    private String topic;
    private String status;

    public EventLog(String id, String payload, String topic, String status) {
        this.id = id;
        this.payload = payload;
        this.timestamp = Instant.now();
        this.topic = topic;
        this.status = status;
    }

    public EventLog() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
