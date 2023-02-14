package com.test.task.productservice;

import lombok.Data;

import java.util.UUID;

@Data
public class Operation {
    private UUID id;

    private String cardNumber;

    private Double amountOfPayment;

    private String status;
}
