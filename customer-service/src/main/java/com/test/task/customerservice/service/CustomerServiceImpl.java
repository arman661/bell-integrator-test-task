package com.test.task.customerservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.task.common.model.BillPaymentResult;
import com.test.task.common.model.OperationStatus;
import com.test.task.common.model.PaymentRequest;
import com.test.task.customerservice.Bill;
import com.test.task.customerservice.CustomerRecord;
import com.test.task.customerservice.entity.Customer;
import com.test.task.customerservice.exception.PaymentException;
import com.test.task.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gateway.host}")
    private String gatewayHost;

    @Override
    public Customer save(CustomerRecord customerRecord) {
        Customer customer = toEntity(customerRecord);
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(CustomerRecord customerRecord) {
        Customer customer = customerRepository.findById(customerRecord.getId()).orElseThrow(()
                -> new IllegalArgumentException("Can't find Customer with id = " + customerRecord.getId()));
        if (customerRecord.getName() != null) {
            customer.setName(customerRecord.getName());
        }
        if (customerRecord.getPhoneNumber() != null) {
            customer.setPhoneNumber(customerRecord.getPhoneNumber());
        }
        if (customerRecord.getBankAccount() != null) {
            customer.setBankAccount(customerRecord.getBankAccount());
        }
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public ResponseEntity<BillPaymentResult> billPayment(Bill bill) {
        Customer customer = customerRepository.findById(bill.getClientId())
                .orElseThrow(EntityNotFoundException::new);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBillUuid(bill.getBillUuid());
        paymentRequest.setCardNumber(customer.getBankAccount());
        paymentRequest.setTotalSum(bill.getTotalSum());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "";
        try {
            body = objectMapper.writeValueAsString(paymentRequest);
        } catch (Exception e) {
            log.error("Can't parse PaymentRequest to json {}", paymentRequest);
            throw new IllegalArgumentException("Unable to write PaymentRequest to json");
        }
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        final ResponseEntity<BillPaymentResult> responseEntity = new RestTemplate()
                .postForEntity(gatewayHost + "/bank/billPayment", request
                        , BillPaymentResult.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null
                || OperationStatus.SUCCEED != responseEntity.getBody().getStatus()) {
            throw new PaymentException();
        }
        return responseEntity;
    }

    private Customer toEntity(CustomerRecord customerRecord) {
        Customer customer = new Customer();
        customer.setName(customerRecord.getName());
        customer.setPhoneNumber(customerRecord.getPhoneNumber());
        customer.setBankAccount(customerRecord.getBankAccount());
        return customer;
    }
}
