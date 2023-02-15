package com.test.task.customerservice.dto;

import lombok.Data;

@Data
public class CustomerRecord {
    private Long id;
    private String name;
    private String phoneNumber;
    private String bankAccount;
}
