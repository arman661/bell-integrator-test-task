package com.test.task.customerservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.task.customerservice.Bill;
import com.test.task.customerservice.CustomerRecord;
import com.test.task.customerservice.PaymentRequest;
import com.test.task.customerservice.entity.Customer;
import com.test.task.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public Customer save(CustomerRecord customerRecord) {
        Customer customer = toEntity(customerRecord);
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(CustomerRecord customerRecord) {
        Customer customer = customerRepository.findById(customerRecord.getId()).orElseThrow(()
                -> new IllegalArgumentException("Cannot find Customer with id = " + customerRecord.getId()));
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
    public ResponseEntity<String> billPayment(Bill bill) {
        Customer customer = customerRepository.findById(bill.getClientId()).orElseThrow(EntityNotFoundException::new);
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBillUuid(bill.getBillUuid());
        paymentRequest.setCardNumber(customer.getBankAccount());
        paymentRequest.setTotalSum(bill.getTotalSum());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(paymentRequest);
        } catch (Exception e) {

        }
        HttpEntity<String> request = new HttpEntity<>(body, headers);


        return new RestTemplate()
                        .postForEntity("http://localhost:8086/bank/billPayment", request
                                , String.class);
    }

    private Customer toEntity(CustomerRecord customerRecord) {
        Customer customer = new Customer();
        customer.setName(customerRecord.getName());
        customer.setPhoneNumber(customerRecord.getPhoneNumber());
        customer.setBankAccount(customerRecord.getBankAccount());
        return customer;
    }
}
