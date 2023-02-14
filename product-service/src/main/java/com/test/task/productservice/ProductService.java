package com.test.task.productservice;

import com.test.task.productservice.controller.Order;

import java.util.List;

public interface ProductService {
    Product save(ProductRecord productRecord);

    void delete(Long id);

    Product update(ProductRecord productRecord);

    List<Product> getAll();

    ProductResponse buy(Order order) throws Exception;
}
