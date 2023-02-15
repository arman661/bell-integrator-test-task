package com.test.task.productservice.dto;

import lombok.Data;

@Data
public class ProductRecord {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
}
