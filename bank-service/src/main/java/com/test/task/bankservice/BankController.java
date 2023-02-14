package com.test.task.bankservice;

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

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> save(@RequestBody AccountRecord accountRecord) {
        try {
            Account account = bankService.save(accountRecord);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/billPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Operation> billPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            bankService.billPayment(paymentRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/confirmPayment")
    public Boolean confirmPayment(@RequestBody UUID billUuid) {
        return bankService.confirmPayment(billUuid);
    }
 }
