package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.AuthResult;
import com.pragma.powerup.domain.model.LoginCommand;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IAuthenticationPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Use Case Tests")
class AuthenticationUseCaseTest {

    @Mock
    private IAuthenticationPort authenticationPort;

    @Mock
    private IUserPersistencePort userPersistencePort;

    private AuthenticationUseCase authenticationUseCase;

    @BeforeEach
    void setUp() {
        authenticationUseCase = new AuthenticationUseCase(authenticationPort, userPersistencePort);
    }

    @Test
    @DisplayName("HU-5: Should login successfully with valid credentials")
    void login_WhenValidCredentials_ShouldReturnAuthResult() {
        LoginCommand command = new LoginCommand("admin@plazoleta.com", "admin123");

        User user = User.builder()
                .id(1L)
                .correo("admin@plazoleta.com")
                .rol(Role.ADMIN)
                .build();

        AuthResult expectedResult = new AuthResult("jwt-token", "admin@plazoleta.com", "ADMIN", 86400000L);

        when(authenticationPort.authenticate("admin@plazoleta.com", "admin123")).thenReturn(true);
        when(userPersistencePort.findUserByCorreo("admin@plazoleta.com")).thenReturn(Optional.of(user));
        when(authenticationPort.generateToken("admin@plazoleta.com", 1L, "ADMIN")).thenReturn(expectedResult);

        AuthResult result = authenticationUseCase.login(command);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("admin@plazoleta.com", result.getCorreo());
        assertEquals("ADMIN", result.getRol());
        verify(authenticationPort).authenticate("admin@plazoleta.com", "admin123");
        verify(authenticationPort).generateToken("admin@plazoleta.com", 1L, "ADMIN");
    }

    @Test
    @DisplayName("HU-5: Should throw when email is null")
    void login_WhenEmailNull_ShouldThrowException() {
        LoginCommand command = new LoginCommand(null, "password");

        DomainException exception = assertThrows(
                DomainException.class,
                () -> authenticationUseCase.login(command)
        );
        assertTrue(exception.getMessage().contains("correo"));
    }

    @Test
    @DisplayName("HU-5: Should throw when email is empty")
    void login_WhenEmailEmpty_ShouldThrowException() {
        LoginCommand command = new LoginCommand("  ", "password");

        DomainException exception = assertThrows(
                DomainException.class,
                () -> authenticationUseCase.login(command)
        );
        assertTrue(exception.getMessage().contains("correo"));
    }

    @Test
    @DisplayName("HU-5: Should throw when password is null")
    void login_WhenPasswordNull_ShouldThrowException() {
        LoginCommand command = new LoginCommand("admin@test.com", null);

        DomainException exception = assertThrows(
                DomainException.class,
                () -> authenticationUseCase.login(command)
        );
        assertTrue(exception.getMessage().contains("clave"));
    }

    @Test
    @DisplayName("HU-5: Should throw when password is empty")
    void login_WhenPasswordEmpty_ShouldThrowException() {
        LoginCommand command = new LoginCommand("admin@test.com", "  ");

        DomainException exception = assertThrows(
                DomainException.class,
                () -> authenticationUseCase.login(command)
        );
        assertTrue(exception.getMessage().contains("clave"));
    }

    @Test
    @DisplayName("HU-5: Should throw when user not found after authentication")
    void login_WhenUserNotFoundAfterAuth_ShouldThrowException() {
        LoginCommand command = new LoginCommand("unknown@test.com", "password");

        when(authenticationPort.authenticate(anyString(), anyString())).thenReturn(true);
        when(userPersistencePort.findUserByCorreo("unknown@test.com")).thenReturn(Optional.empty());

        DomainException exception = assertThrows(
                DomainException.class,
                () -> authenticationUseCase.login(command)
        );
        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    @DisplayName("HU-5: Should propagate authentication failure")
    void login_WhenBadCredentials_ShouldPropagateException() {
        LoginCommand command = new LoginCommand("admin@test.com", "wrongpass");

        when(authenticationPort.authenticate("admin@test.com", "wrongpass"))
                .thenThrow(new DomainException("Credenciales inválidas"));

        assertThrows(DomainException.class, () -> authenticationUseCase.login(command));
    }
}