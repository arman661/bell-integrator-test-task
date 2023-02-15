package com.test.task.productservice.dto;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String productName;
    private Integer quantity;
}
