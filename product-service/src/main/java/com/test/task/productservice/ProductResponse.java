package com.test.task.productservice;

import com.test.task.productservice.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private List<Product> outOfStock;
}
