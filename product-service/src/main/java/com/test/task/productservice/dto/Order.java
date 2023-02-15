package com.test.task.productservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private Long clientId;
    private List<Item> items;
}
