package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.domain.api.IAuthenticationService;
import com.pragma.powerup.domain.model.AuthResult;
import com.pragma.powerup.domain.model.LoginCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHandler implements IAuthHandler {

    private final IAuthenticationService authenticationService;

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        // DTO → Modelo de dominio
        LoginCommand command = new LoginCommand(
                loginRequest.getCorreo(),
                loginRequest.getClave()
        );

        // Llamamos al UseCase con modelo de dominio
        AuthResult result = authenticationService.login(command);

        // Modelo de dominio → DTO de respuesta
        return AuthResponseDto.builder()
                .token(result.getToken())
                .type("Bearer")
                .correo(result.getCorreo())
                .rol(result.getRol())
                .expiresIn(result.getExpiresIn())
                .build();
    }
}