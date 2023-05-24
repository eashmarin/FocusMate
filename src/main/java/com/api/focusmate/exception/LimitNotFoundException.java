package com.api.focusmate.exception;

public class LimitNotFoundException extends RuntimeException {
    public  LimitNotFoundException(String message) {
        super(message);
    }
}
