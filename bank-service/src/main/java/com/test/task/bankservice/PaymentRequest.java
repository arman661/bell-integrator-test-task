package com.test.task.bankservice;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {
    private UUID billUuid;
    private String cardNumber;
    private Double totalSum;
}
