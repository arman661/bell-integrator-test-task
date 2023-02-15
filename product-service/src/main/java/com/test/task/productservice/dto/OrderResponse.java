package com.test.task.productservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderResponse {
    private List<ProductRecord> outOfStock = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private Boolean succeed;

    public static OrderResponse createResponse(boolean succeed) {
        OrderResponse response = new OrderResponse();
        response.setSucceed(succeed);
        return response;
    }

    public OrderResponse addError(String errorMessage) {
        errors.add(errorMessage);
        return this;
    }
}
