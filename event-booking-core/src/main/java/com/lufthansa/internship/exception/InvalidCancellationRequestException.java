package com.lufthansa.internship.exception;

public class InvalidCancellationRequestException extends RuntimeException {
    public InvalidCancellationRequestException(String message) {
        super(message);
    }
}
