package com.test.task.bankservice.service;

import com.test.task.bankservice.AccountRecord;
import com.test.task.bankservice.NotEnoughFundsException;
import com.test.task.bankservice.entity.Account;
import com.test.task.bankservice.entity.Bill;
import com.test.task.bankservice.repository.AccountRepository;
import com.test.task.bankservice.repository.BillRepository;
import com.test.task.common.model.OperationStatus;
import com.test.task.common.model.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService{
    private final AccountRepository accountRepository;
    private final BillRepository billRepository;

    @Override
    public Account save(AccountRecord accountRecord) {
        Account customer = toEntity(accountRecord);
        return accountRepository.save(customer);
    }

    @Override
    @Transactional
    public void billPayment(PaymentRequest paymentRequest) {
        Account account = accountRepository.findByCardNumber(paymentRequest.getCardNumber())
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Account %s doesn't exist", paymentRequest.getCardNumber())));
        if (account.getBalance() < paymentRequest.getTotalSum()) {
            throw new NotEnoughFundsException("Not enough money in your account");
        }
        Bill bill = new Bill();
        bill.setId(paymentRequest.getBillUuid());
        bill.setAmountOfPayment(paymentRequest.getTotalSum());
        bill.setAccount(account);
        bill.setStatus(OperationStatus.SUCCEED.toString());
        billRepository.save(bill);

        account.setBalance(account.getBalance() - paymentRequest.getTotalSum());
        accountRepository.save(account);

    }

    @Override
    public Boolean confirmPayment(UUID billUuid) {
        return billRepository.findStatusById(billUuid)
                .map(bill -> OperationStatus.SUCCEED.name().equals(bill.getStatus()))
                .orElse(false);
    }

    private Account toEntity(AccountRecord accountRecord) {
        Account account = new Account();
        account.setCardNumber(accountRecord.getCardNumber());
        account.setBalance(accountRecord.getBalance());
        return account;
    }
}
