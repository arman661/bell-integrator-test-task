package com.test.task.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.task.productservice.controller.Item;
import com.test.task.productservice.controller.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Product save(ProductRecord productRecord) {
        Product product = toEntity(productRecord);
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product update(ProductRecord productRecord) {
        Product product = productRepository.findById(productRecord.getId()).orElseThrow(()
                -> new IllegalArgumentException("Cannot find Product with id = " + productRecord.getId()));
        if (productRecord.getName() != null) {
            product.setName(productRecord.getName());
        }
        if (productRecord.getPrice() != null) {
            product.setPrice(productRecord.getPrice());
        }
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    private Product toEntity(ProductRecord productRecord) {
        Product product = new Product();
        product.setName(productRecord.getName());
        product.setPrice(productRecord.getPrice());
        product.setQuantity(productRecord.getQuantity());
        return product;
    }

    @Override
    public ProductResponse buy(Order order) throws Exception {
        Map<Long, Product> products = productRepository.findAllById(
                order.getItems().stream().map(Item::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Product::getId, p -> p));
        List<Product> outOfStock = writeOff(order.getItems(), products);
        ProductResponse productResponse = new ProductResponse();
        if (!outOfStock.isEmpty()) {
            productResponse.setOutOfStock(outOfStock);
            return productResponse;
            // TODO prepare error response
        }
        products.values().forEach(productRepository::save);

        Bill bill = prepareBill(order, products);

        ResponseEntity<String> responseEntity = sendBillToCustomer(bill);

        String body = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Operation operation = objectMapper.readValue(body, Operation.class);
        boolean confirmPayment = confirmPayment(bill.getBillUuid());
        if (!operation.getStatus().equals("SUCCEED") && confirmPayment) {
            throw new Exception();
        }
        return null;
    }

    private Boolean confirmPayment(UUID billUuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(billUuid);
        } catch (JsonProcessingException e) {
            log.error("Cannot parse Order to json");
        }
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        return new RestTemplate()
                .postForEntity("http://localhost:8765/bank/confirmPayment", request
                        , Boolean.class).getBody();
    }

    private ResponseEntity<String> sendBillToCustomer(Bill bill) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {

            body = objectMapper.writeValueAsString(bill);
        } catch (JsonProcessingException e) {
            log.error("Cannot parse Order to json");
        }
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        return new RestTemplate()
                .postForEntity("http://localhost:8765/customer/billPayment", request
                        , String.class);
    }

    private Bill prepareBill(Order order, Map<Long, Product> products) {
        Bill bill = new Bill();
        bill.setBillUuid(UUID.randomUUID());
        bill.setClientId(order.getClientId());
        Double totalSum = order.getItems()
                .stream().map(item -> item.getQuantity() * products.get(item.getId()).getPrice())
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalStateException("Unable to count total sum"));
        bill.setTotalSum(totalSum);
        return bill;
    }

    private List<Product> writeOff(List<Item> items, Map<Long, Product> products) {
        List<Product> outOfStock = new ArrayList<>();
        for (Item item : items) {
            Product product = products.get(item.getId());
            if (product.getQuantity() >= item.getQuantity()) {
                product.setQuantity(product.getQuantity() - item.getQuantity());
            } else {
                outOfStock.add(product);
            }
        }
        return outOfStock;
    }
}
