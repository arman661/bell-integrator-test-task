package com.test.task.productservice.exception;

public class NoBankConfirmationException extends RuntimeException {
    public NoBankConfirmationException(String message) {
        super(message);
    }
}
