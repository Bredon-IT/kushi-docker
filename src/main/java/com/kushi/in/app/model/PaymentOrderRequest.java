package com.kushi.in.app.model;

import lombok.Data;

@Data
public class PaymentOrderRequest {
    private Integer amount;  // Amount in paise (e.g., 50000 = ₹500)
    private String currency; // Default: INR
}
