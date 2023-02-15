package com.test.task.common.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BillRequest {
    private UUID billUuid;
    private Long clientId;
    private Double totalSum;
}
