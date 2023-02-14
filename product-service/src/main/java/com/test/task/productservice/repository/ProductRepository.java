package com.test.task.productservice.repository;

import com.test.task.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Integer findQuantityByName(String productName);
}