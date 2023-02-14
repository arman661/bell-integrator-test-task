package com.test.task.bankservice;

import java.util.UUID;

public interface BankService {
    Account save(AccountRecord accountRecord);

    void billPayment(PaymentRequest paymentRequest) throws Exception;

    Boolean confirmPayment(UUID billUuid);
}
