package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "canonical_transactions")
public class CanonicalTransaction {
    @Id
    private String id;
    private String posType;
    private String storeId;
    private String transactionId;
    private double amount;
    private LocalDateTime timestamp;
}
