package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.producer.CanonicalKafkaProducer;
import com.example.demo.repository.CanonicalTransactionRepository;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.UUID;

import com.opencsv.CSVReader;

@Service
public class CanonicalService {

    private final CanonicalKafkaProducer producer;
    private final CanonicalTransactionRepository repository;

    public CanonicalService(CanonicalKafkaProducer producer, CanonicalTransactionRepository repository) {
        this.producer = producer;
        this.repository = repository;
    }

    // ✅ 1. JSON → Canonical
    public void processStoreLine(StoreLineTransaction txn) {
        CanonicalTransaction canonical = new CanonicalTransaction();
        canonical.setId(UUID.randomUUID().toString());
        canonical.setPosType("STORELINE");
        canonical.setStoreId(txn.getStoreCode());
        canonical.setTransactionId(txn.getTxnId());
        canonical.setAmount(txn.getTotalPrice());
        canonical.setTimestamp(LocalDateTime.now());

        producer.sendCanonicalEvent(canonical);
        repository.save(canonical);
        System.out.println("💾 [STORELINE] Saved canonical transaction to MongoDB");
    }

    // ✅ 2. XML → Canonical
    public void processToshiba(ToshibaTransaction txn) {
        CanonicalTransaction canonical = new CanonicalTransaction();
        canonical.setId(UUID.randomUUID().toString());
        canonical.setPosType("TOSHIBA");
        canonical.setStoreId(txn.getLocationId());
        canonical.setTransactionId(txn.getTransactionNumber());
        canonical.setAmount(txn.getAmount());
        canonical.setTimestamp(LocalDateTime.now());

        producer.sendCanonicalEvent(canonical);
        repository.save(canonical);
        System.out.println("💾 [TOSHIBA] Saved canonical transaction to MongoDB");
    }

    // ✅ 3. CSV → Canonical (improved version)
    public void processEmeraldCSV(String csv) {
        // expected format:
        // shopId,orderId,billValue
        // E001,ORD-555,320.75
        try (CSVReader reader = new CSVReader(new StringReader(csv.trim()))) {
            String[] header = reader.readNext(); // read header if present
            String[] data = reader.readNext();   // read first record

            if (data == null || data.length < 3) {
                throw new IllegalArgumentException("Invalid CSV input: expected shopId,orderId,billValue");
            }

            CanonicalTransaction canonical = new CanonicalTransaction();
            canonical.setId(UUID.randomUUID().toString());
            canonical.setPosType("EMERALD");
            canonical.setStoreId(data[0].trim());
            canonical.setTransactionId(data[1].trim());
            canonical.setAmount(Double.parseDouble(data[2].trim()));
            canonical.setTimestamp(LocalDateTime.now());

            producer.sendCanonicalEvent(canonical);
            repository.save(canonical);
            System.out.println("💾 [EMERALD] Saved canonical transaction to MongoDB");

        } catch (Exception e) {
            System.err.println("❌ Error processing Emerald CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
