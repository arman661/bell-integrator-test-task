package com.test.task.productservice;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private List<Product> outOfStock;
}
