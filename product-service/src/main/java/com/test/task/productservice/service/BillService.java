package com.test.task.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.task.common.model.BillPaymentResult;
import com.test.task.common.model.BillRequest;
import com.test.task.common.model.OperationStatus;
import com.test.task.productservice.Order;
import com.test.task.productservice.entity.Product;
import com.test.task.productservice.exception.PaymentException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@Log4j2
public class BillService {

    @Value("${gateway.host}")
    private String gatewayHost;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BillRequest prepareBill(Order order, Map<Long, Product> products) {
        BillRequest billRequest = new BillRequest();
        billRequest.setBillUuid(UUID.randomUUID());
        billRequest.setClientId(order.getClientId());
        Double totalSum = order.getItems()
                .stream().map(item -> item.getQuantity() * products.get(item.getId()).getPrice())
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalStateException("Unable to count total sum"));
        billRequest.setTotalSum(totalSum);
        return billRequest;
    }

    public void sendBillToCustomer(BillRequest billRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(billRequest);
        } catch (JsonProcessingException e) {
            log.error("Can't parse Order to json {}", billRequest);
            throw new IllegalArgumentException("Unable to write bill to json");
        }

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<BillPaymentResult> responseEntity = new RestTemplate()
                .postForEntity(gatewayHost + "/customer/billPayment", request
                        , BillPaymentResult.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null
                    || OperationStatus.SUCCEED != responseEntity.getBody().getStatus()) {
            throw new PaymentException();
        }
    }

    public BillPaymentResult confirmPayment(UUID billUuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "";
        try {
            body = objectMapper.writeValueAsString(billUuid);
        } catch (JsonProcessingException e) {
            log.error("Can't parse Order to json");
        }
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        return new RestTemplate()
                .getForEntity(gatewayHost + "/bank/confirmPayment?uuid=" + billUuid.toString(), BillPaymentResult.class).getBody();
    }
}
