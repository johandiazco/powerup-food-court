package com.pragma.powerup.domain.model;

public class LoginCommand {

    private final String correo;
    private final String clave;

    public LoginCommand(String correo, String clave) {
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