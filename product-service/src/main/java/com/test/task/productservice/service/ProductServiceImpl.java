package com.test.task.productservice.service;

import com.test.task.common.model.BillPaymentResult;
import com.test.task.common.model.BillRequest;
import com.test.task.common.model.OperationStatus;
import com.test.task.productservice.Item;
import com.test.task.productservice.Order;
import com.test.task.productservice.OrderResponse;
import com.test.task.productservice.ProductRecord;
import com.test.task.productservice.entity.Product;
import com.test.task.productservice.exception.NoBankConfirmationException;
import com.test.task.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BillService billService;

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
        if (productRecord == null || productRecord.getId() == null) {
            throw new IllegalArgumentException("Can't update product without ID");
        }
        Product product = productRepository.findById(productRecord.getId()).orElseThrow(()
                -> new IllegalArgumentException("Can't find Product with id = " + productRecord.getId()));
        if (productRecord.getName() != null) {
            product.setName(productRecord.getName());
        }
        if (productRecord.getPrice() != null) {
            product.setPrice(productRecord.getPrice());
        }
        if (productRecord.getQuantity() != null) {
            product.setQuantity(productRecord.getQuantity());
        }
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public OrderResponse buy(Order order) {
        List<Long> productIds = order.getItems().stream().map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, Product> products = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, p -> p));

        List<ProductRecord> outOfStock = writeOff(order.getItems(), products);
        if (!outOfStock.isEmpty()) {
            return OrderResponse.createResponse(false).setOutOfStock(outOfStock);
        }
        products.values().forEach(productRepository::save);

        BillRequest billRequest = billService.prepareBill(order, products);
        billService.sendBillToCustomer(billRequest);

        BillPaymentResult confirmPayment = billService.confirmPayment(billRequest.getBillUuid());
        if (!OperationStatus.SUCCEED.equals(confirmPayment.getStatus())) {
            throw new NoBankConfirmationException("The bank did not confirm the transaction");
        }

        return OrderResponse.createResponse(true);
    }

    private List<ProductRecord> writeOff(List<Item> items, Map<Long, Product> products) {
        List<ProductRecord> outOfStock = new ArrayList<>();
        for (Item item : items) {
            Product product = products.get(item.getId());
            if (product.getQuantity() >= item.getQuantity()) {
                product.setQuantity(product.getQuantity() - item.getQuantity());
            } else {
                ProductRecord productRecord = getProductRecord(product);
                outOfStock.add(productRecord);
            }
        }
        return outOfStock;
    }

    private Product toEntity(ProductRecord productRecord) {
        Product product = new Product();
        product.setName(productRecord.getName());
        product.setPrice(productRecord.getPrice());
        product.setQuantity(productRecord.getQuantity());
        return product;
    }

    private ProductRecord getProductRecord(Product product) {
        ProductRecord productRecord = new ProductRecord();
        productRecord.setId(product.getId());
        productRecord.setName(product.getName());
        productRecord.setQuantity(product.getQuantity());
        productRecord.setPrice(product.getPrice());
        return productRecord;
    }
}
