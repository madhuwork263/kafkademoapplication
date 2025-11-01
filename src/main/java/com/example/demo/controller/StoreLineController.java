package com.example.demo.controller;

import com.example.demo.service.StoreLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for StoreLine API (Local)
 * ------------------------------------------
 * Handles incoming XML payloads and passes them to StoreLineService.
 */
@RestController
@RequestMapping("/api/storeline")
public class StoreLineController {

    private static final Logger log = LoggerFactory.getLogger(StoreLineController.class);

    @Autowired
    private StoreLineService storeLineService;

    @PostMapping(value = "/send", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<String> sendXml(@RequestBody String xml) {
        try {
            log.info("🌐 Received XML payload from API...");
            storeLineService.processIncomingXml(xml);
            return ResponseEntity.ok("{\"message\": \"✅ XML processed successfully and sent to Kafka + MongoDB\"}");
        } catch (Exception e) {
            log.error("❌ Error processing XML: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Failed to process XML: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("💓 Health check called");
        return ResponseEntity.ok("StoreLine API is running ✅");
    }
}
