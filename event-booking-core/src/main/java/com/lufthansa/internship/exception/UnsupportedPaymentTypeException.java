package com.lufthansa.internship.exception;

public class UnsupportedPaymentTypeException extends RuntimeException {
    public UnsupportedPaymentTypeException(String message) {
        super(message);
    }
}
