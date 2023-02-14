package com.test.task.bankservice;

import lombok.Data;

@Data
public class AccountRecord {
    private Long id;
    private String cardNumber;
    private Double balance;
}
