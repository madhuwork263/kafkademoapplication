package com.example.demo.model;

import lombok.Data;

@Data
public class EmeraldTransaction {
    private String shopId;
    private String orderId;
    private double billValue;
}
