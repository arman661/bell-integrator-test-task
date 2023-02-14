package com.test.task.customerservice;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private List<Unit> units;
}
