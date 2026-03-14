package com.pragma.powerup.domain.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(Long id) {
        super("No se encontró el usuario con ID: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
