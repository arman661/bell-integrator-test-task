package com.test.task.customerservice;

import lombok.Data;

import java.awt.*;
import java.util.UUID;

@Data
public class PaymentRequest {
    private UUID billUuid;
    private String cardNumber;
    private Double totalSum;
}
