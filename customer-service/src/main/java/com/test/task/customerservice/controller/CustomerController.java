package com.test.task.customerservice.controller;

import com.test.task.common.model.BillPaymentResult;
import com.test.task.customerservice.dto.Bill;
import com.test.task.customerservice.dto.CustomerRecord;
import com.test.task.customerservice.entity.Customer;
import com.test.task.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Log4j2
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> save(@RequestBody CustomerRecord customerRecord) {
        try {
            Customer customer = customerService.save(customerRecord);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(value = "/remove")
    public ResponseEntity<Customer> delete(@RequestParam("id") Long id) {
        customerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> update(@RequestBody CustomerRecord customerRecord) {
        try {
            Customer customer = customerService.update(customerRecord);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            log.error(e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get")
    public List<Customer> getAll() {
        return customerService.getAll();
    }

    @PostMapping(value = "/billPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BillPaymentResult> billPayment(@RequestBody Bill bill) {
        return customerService.billPayment(bill);
    }
}
