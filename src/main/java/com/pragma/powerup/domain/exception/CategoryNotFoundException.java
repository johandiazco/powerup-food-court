package com.pragma.powerup.domain.exception;

public class CategoryNotFoundException extends DomainException {

    public CategoryNotFoundException(Long id) {
        super("No se encontró la categoría con ID: " + id);
    }
}