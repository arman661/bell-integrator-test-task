package com.test.task.bankservice.controller;

import com.test.task.bankservice.dto.AccountRecord;
import com.test.task.bankservice.entity.Account;
import com.test.task.bankservice.service.BankService;
import com.test.task.common.model.BillPaymentResult;
import com.test.task.common.model.OperationStatus;
import com.test.task.common.model.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bank")
@RequiredArgsConstructor
@Log4j2
public class BankController {
    private final BankService bankService;

    @PostMapping
    public ResponseEntity<Account> save(@RequestBody AccountRecord accountRecord) {
        try {
            Account account = bankService.save(accountRecord);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/billPayment")
    public ResponseEntity<BillPaymentResult> billPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            bankService.billPayment(paymentRequest);
            return ResponseEntity.ok(new BillPaymentResult(paymentRequest.getBillUuid()
                    , OperationStatus.SUCCEED));
        } catch (Exception e) {
            log.error(e);
            return ResponseEntity.internalServerError().body(new BillPaymentResult(paymentRequest.getBillUuid()
                    , OperationStatus.FAILED));
        }
    }

    @GetMapping(value = "/confirmPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BillPaymentResult> confirmPayment(@RequestParam("uuid") String billUuidParam) {
        UUID billUuid = UUID.fromString(billUuidParam);
        try {
            if (bankService.confirmPayment(billUuid)) {
                return ResponseEntity.ok(new BillPaymentResult(billUuid
                        , OperationStatus.SUCCEED));
            }
        } catch (Exception e) {
            log.error(e);
            return ResponseEntity.internalServerError().body(new BillPaymentResult(billUuid
                    , OperationStatus.FAILED));
        }
        return ResponseEntity.ok(new BillPaymentResult(billUuid
                , OperationStatus.FAILED));
    }
 }
