package com.test.task.productservice;

public class NoBankConfirmationException extends RuntimeException{
    public NoBankConfirmationException(String message) {
        super(message);
    }
}
