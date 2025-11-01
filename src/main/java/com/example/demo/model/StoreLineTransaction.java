package com.example.demo.model;

import lombok.Data;

@Data
public class StoreLineTransaction {
    private String storeCode;
    private String txnId;
    private double totalPrice;
}
