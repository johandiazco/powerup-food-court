package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.AuthResult;

public interface IAuthenticationPort {
    boolean authenticate(String correo, String clave);
    AuthResult generateToken(String correo, Long userId, String rol);
}