package com.pragma.powerup.domain.model;

public class LoginCommand {

    private final String correo;
    private final String clave;

    public LoginCommand(String correo, String clave) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (clave == null || clave.trim().isEmpty()) {
            throw new IllegalArgumentException("La clave es obligatoria");
        }
        this.correo = correo;
        this.clave = clave;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClave() {
        return clave;
    }
}