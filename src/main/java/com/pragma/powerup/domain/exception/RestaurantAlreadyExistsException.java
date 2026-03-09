package com.pragma.powerup.domain.exception;

public class RestaurantAlreadyExistsException extends DomainException {

    private static final String MESSAGE_TEMPLATE = "Ya existe un restaurante con el NIT: %s";

    public RestaurantAlreadyExistsException(String nit) {
        super(java.lang.String.format(MESSAGE_TEMPLATE, nit));
    }
}