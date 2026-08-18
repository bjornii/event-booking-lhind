package com.lufthansa.internship.exception;

public class SeatLimitExceedingException extends RuntimeException {
    public SeatLimitExceedingException(String message) {
        super(message);
    }
}
