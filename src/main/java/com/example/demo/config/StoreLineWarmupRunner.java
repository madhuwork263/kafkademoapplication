package com.example.demo.config;

import com.example.demo.service.StoreLineService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Warms up StoreLine components during startup to prevent
 * first-request delay or missing initial logs.
 */
@Component
public class StoreLineWarmupRunner {

    private static final Logger log = LoggerFactory.getLogger(StoreLineWarmupRunner.class);
    private final StoreLineService storeLineService;

    public StoreLineWarmupRunner(StoreLineService storeLineService) {
        this.storeLineService = storeLineService;
    }

    @PostConstruct
    public void warmup() {
        log.info("🚀 Initializing StoreLine components...");
        storeLineService.initialize();
        log.info("✅ StoreLine warmup complete. Ready for XML payloads.");
    }
}
