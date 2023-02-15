package com.test.task.productservice;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderResponse {
    private List<ProductRecord> outOfStock;
    private List<String> errors;
    private Boolean succeed;

    public static OrderResponse createResponse(boolean succeed) {
        OrderResponse response = new OrderResponse();
        response.setSucceed(succeed);
        return response;
    }

    public OrderResponse addError(String errorMessage) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(errorMessage);
        return this;
    }
}
