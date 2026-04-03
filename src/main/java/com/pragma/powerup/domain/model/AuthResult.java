package com.pragma.powerup.domain.model;

public class AuthResult {

    private final String token;
    private final String correo;
    private final String rol;
    private final long expiresIn;

    public AuthResult(String token, String correo, String rol, long expiresIn) {
        this.token = token;
        this.correo = correo;
        this.rol = rol;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}