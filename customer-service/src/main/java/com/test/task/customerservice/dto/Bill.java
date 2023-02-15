package com.test.task.customerservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class Bill {
    private UUID billUuid;
    private Long clientId;
    private Double totalSum;
}
