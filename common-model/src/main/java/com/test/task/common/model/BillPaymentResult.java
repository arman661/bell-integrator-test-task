package com.test.task.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillPaymentResult {
    private UUID id;

    private OperationStatus status;
}
