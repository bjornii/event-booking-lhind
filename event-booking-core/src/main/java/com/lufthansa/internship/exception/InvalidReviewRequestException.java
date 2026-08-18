package com.lufthansa.internship.exception;

public class InvalidReviewRequestException extends RuntimeException {
    public InvalidReviewRequestException(String message) {
        super(message);
    }
}
