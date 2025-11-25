package com.example.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private String razorpayOrderId;
    private String razorpayKey;
    private Integer amountCents;
    private String currency;
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
