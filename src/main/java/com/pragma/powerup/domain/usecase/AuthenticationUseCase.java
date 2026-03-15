package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;
import com.pragma.powerup.domain.api.IAuthenticationService;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@RequiredArgsConstructor
public class AuthenticationUseCase implements IAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final IUserPersistencePort userPersistencePort;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        try {
            //Autenticamos usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCorreo(),
                            loginRequest.getClave()
                    )
            );

            //Cargamos UserDetails
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCorreo());

            //Obtenemos datos del usuario desde BD
            User user = userPersistencePort.findUserByCorreo(loginRequest.getCorreo())
                    .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

            //Generamos token JWT CON userId
            String jwtToken = jwtService.generateToken(userDetails, user.getId());

            //Construimos respuesta
            return AuthResponseDto.builder()
                    .token(jwtToken)
                    .type("Bearer")
                    .correo(user.getCorreo())
                    .rol(user.getRol().name())
                    .expiresIn(jwtExpiration)
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales inválidas: correo o contraseña incorrectos");
        }
    }
}