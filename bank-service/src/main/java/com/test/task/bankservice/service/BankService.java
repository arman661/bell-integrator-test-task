package com.test.task.bankservice.service;

import com.test.task.bankservice.dto.AccountRecord;
import com.test.task.bankservice.entity.Account;
import com.test.task.common.model.PaymentRequest;

import java.util.UUID;

public interface BankService {
    Account save(AccountRecord accountRecord);

    void billPayment(PaymentRequest paymentRequest) throws Exception;

    Boolean confirmPayment(UUID billUuid);
}
