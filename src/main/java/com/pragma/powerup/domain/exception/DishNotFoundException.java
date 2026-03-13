package com.pragma.powerup.domain.exception;

public class DishNotFoundException extends DomainException {

    public DishNotFoundException(Long id) {
        super("No se encontró el plato con ID: " + id);
    }
}