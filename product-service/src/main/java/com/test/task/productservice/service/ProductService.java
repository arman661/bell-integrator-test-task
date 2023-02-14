package com.test.task.productservice.service;

import com.test.task.productservice.Order;
import com.test.task.productservice.ProductRecord;
import com.test.task.productservice.ProductResponse;
import com.test.task.productservice.entity.Product;

import java.util.List;

public interface ProductService {
    Product save(ProductRecord productRecord);

    void delete(Long id);

    Product update(ProductRecord productRecord);

    List<Product> getAll();

    ProductResponse buy(Order order) throws Exception;
}
