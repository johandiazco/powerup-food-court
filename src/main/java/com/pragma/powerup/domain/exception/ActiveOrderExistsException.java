package com.pragma.powerup.domain.exception;

public class ActiveOrderExistsException extends RuntimeException {
    public ActiveOrderExistsException(String message) {
        super(message);
    }
}