package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.AuthResult;
import com.pragma.powerup.domain.spi.IAuthenticationPort;
import com.pragma.powerup.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements IAuthenticationPort {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public boolean authenticate(String correo, String clave) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, clave)
            );
            return true;
        } catch (BadCredentialsException e) {
            throw new DomainException("Credenciales inválidas: correo o contraseña incorrectos");
        }
    }

    @Override
    public AuthResult generateToken(String correo, Long userId, String rol) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(correo);
        String jwtToken = jwtService.generateToken(userDetails, userId);

        return new AuthResult(jwtToken, correo, rol, jwtExpiration);
    }
}