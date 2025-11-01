package com.example.demo.repository;

import com.example.demo.model.EventLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogRepository extends MongoRepository<EventLog, String> {
}
