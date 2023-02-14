package com.test.task.productservice;

import lombok.Data;

@Data
public class ProductRecord {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
}
