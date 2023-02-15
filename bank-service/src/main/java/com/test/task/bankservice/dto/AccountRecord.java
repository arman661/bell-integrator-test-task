package com.test.task.bankservice.dto;

import lombok.Data;

@Data
public class AccountRecord {
    private Long id;
    private String cardNumber;
    private Double balance;
}
