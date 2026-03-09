package com.pragma.powerup.domain.exception;

public class RestaurantNotFoundException extends DomainException {

    private static final String MESSAGE_TEMPLATE = "No se encontró el restaurante con ID: %d";

    public RestaurantNotFoundException(Long id) {
        super(java.lang.String.format(MESSAGE_TEMPLATE, id));
    }
}