package com.lufthansa.internship.exception;

public class UnauthorizedUpdateEventException extends RuntimeException {
    public UnauthorizedUpdateEventException(String message) {
        super(message);
    }
}
