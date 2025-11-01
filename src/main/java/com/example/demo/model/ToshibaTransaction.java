package com.example.demo.model;

import lombok.Data;
import jakarta.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "Transaction")
public class ToshibaTransaction {
    private String locationId;
    private String transactionNumber;
    private double amount;
}
