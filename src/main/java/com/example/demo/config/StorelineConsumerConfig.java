package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class StorelineConsumerConfig {

    @Value("${storeline.consumer.domain.value}")
    private String domainValue;

    @Value("${storeline.consumer.deadletter.value}")
    private String deadletterValue;

    @Value("${storeline.consumer.status.value}")
    private String statusValue;
}
