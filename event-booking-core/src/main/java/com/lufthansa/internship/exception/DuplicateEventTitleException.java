package com.lufthansa.internship.exception;

public class DuplicateEventTitleException extends RuntimeException {
    public DuplicateEventTitleException(String message) {
        super(message);
    }
}
