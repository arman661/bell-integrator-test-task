package com.test.task.customerservice.service;

import com.test.task.common.model.BillPaymentResult;
import com.test.task.customerservice.Bill;
import com.test.task.customerservice.CustomerRecord;
import com.test.task.customerservice.entity.Customer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {
    Customer save(CustomerRecord customerRecord);

    Customer update(CustomerRecord customerRecord);

    void delete(Long id);

    List<Customer> getAll();

    ResponseEntity<BillPaymentResult> billPayment(Bill bill);
}
