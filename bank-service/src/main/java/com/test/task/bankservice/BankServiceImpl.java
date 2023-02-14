package com.test.task.bankservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService{
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    @Override
    public Account save(AccountRecord accountRecord) {
        Account customer = toEntity(accountRecord);
        return accountRepository.save(customer);
    }

    @Override
    public void billPayment(PaymentRequest paymentRequest) throws Exception {
        Account account = accountRepository.findByCardNumber(paymentRequest.getCardNumber()).orElseThrow(EntityNotFoundException::new);
        Operation operation = new Operation();
        operation.setId(paymentRequest.getBillUuid());
        operation.setAmountOfPayment(paymentRequest.getTotalSum());
        operation.getAccount().setCardNumber(paymentRequest.getCardNumber());
        if(account.getBalance() >= paymentRequest.getTotalSum()) {
            account.setBalance(account.getBalance() - paymentRequest.getTotalSum());
            operation.setStatus(OperationStatus.SUCCEED.toString());
        } else {
            throw new Exception("Not enough money in your account");
        }
        operationRepository.save(operation);
    }

    @Override
    public Boolean confirmPayment(UUID billUuid) {
        String status = operationRepository.findStatusById(billUuid).orElseThrow(() -> new EntityNotFoundException());
        return status.equals(OperationStatus.SUCCEED);
    }

    private Account toEntity(AccountRecord accountRecord) {
        Account account = new Account();
        account.setCardNumber(accountRecord.getCardNumber());
        account.setBalance(accountRecord.getBalance());
        return account;
    }
}
