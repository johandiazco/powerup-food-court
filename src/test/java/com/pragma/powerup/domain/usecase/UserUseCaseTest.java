package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.exception.UserAlreadyExistsException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Use Case Tests")
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    private UserUseCase userUseCase;
    private User validUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userPersistencePort);

        validUser = User.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documentoIdentidad("1234567890")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .correo("juan.perez@example.com")
                .clave("password123")
                .build();
    }

    @Test
    @DisplayName("HU-1: Should create propietario with role PROPIETARIO assigned")
    void createPropietario_ShouldAssignRolePropietario() {
        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(userPersistencePort.saveUser(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userUseCase.createPropietario(validUser);

        assertNotNull(result);
        assertEquals(Role.PROPIETARIO, result.getRol());
        verify(userPersistencePort).saveUser(argThat(u -> u.getRol() == Role.PROPIETARIO));
    }

    @Test
    @DisplayName("HU-1 C4: Should reject minor user for propietario")
    void createPropietario_WhenMinor_ShouldThrowException() {
        validUser.setFechaNacimiento(LocalDate.now().minusYears(16));

        DomainException exception = assertThrows(
                DomainException.class,
                () -> userUseCase.createPropietario(validUser)
        );
        assertTrue(exception.getMessage().contains("mayor de edad"));
    }

    @Test
    @DisplayName("HU-1: Should validate email format")
    void createPropietario_WhenInvalidEmail_ShouldThrowException() {
        validUser.setCorreo("correo-invalido");

        DomainException exception = assertThrows(
                DomainException.class,
                () -> userUseCase.createPropietario(validUser)
        );
        assertTrue(exception.getMessage().contains("correo"));
    }

    @Test
    @DisplayName("HU-1: Should validate phone format")
    void createPropietario_WhenInvalidPhone_ShouldThrowException() {
        validUser.setCelular("123");

        DomainException exception = assertThrows(
                DomainException.class,
                () -> userUseCase.createPropietario(validUser)
        );
        assertTrue(exception.getMessage().contains("celular"));
    }

    @Test
    @DisplayName("HU-1: Should validate document is numeric")
    void createPropietario_WhenNonNumericDocument_ShouldThrowException() {
        validUser.setDocumentoIdentidad("abc123");

        DomainException exception = assertThrows(
                DomainException.class,
                () -> userUseCase.createPropietario(validUser)
        );
        assertTrue(exception.getMessage().contains("documento"));
    }

    @Test
    @DisplayName("Should reject duplicate email")
    void createPropietario_WhenEmailExists_ShouldThrowException() {
        when(userPersistencePort.existsByCorreo("juan.perez@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userUseCase.createPropietario(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should reject duplicate document")
    void createPropietario_WhenDocumentExists_ShouldThrowException() {
        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad("1234567890")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userUseCase.createPropietario(validUser));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("HU-6: Should create empleado with role EMPLEADO assigned")
    void createEmpleado_ShouldAssignRoleEmpleado() {
        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(userPersistencePort.saveUser(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(2L);
            return u;
        });

        User result = userUseCase.createEmpleado(validUser);

        assertNotNull(result);
        assertEquals(Role.EMPLEADO, result.getRol());
        verify(userPersistencePort).saveUser(argThat(u -> u.getRol() == Role.EMPLEADO));
    }

    @Test
    @DisplayName("HU-8: Should create cliente with role CLIENTE assigned")
    void createCliente_ShouldAssignRoleCliente() {
        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(userPersistencePort.saveUser(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(3L);
            return u;
        });

        User result = userUseCase.createCliente(validUser);

        assertNotNull(result);
        assertEquals(Role.CLIENTE, result.getRol());
        verify(userPersistencePort).saveUser(argThat(u -> u.getRol() == Role.CLIENTE));
    }

    @Test
    @DisplayName("Should throw when name is empty")
    void createPropietario_WhenNameEmpty_ShouldThrowException() {
        validUser.setNombre("");
        assertThrows(DomainException.class, () -> userUseCase.createPropietario(validUser));
    }

    @Test
    @DisplayName("Should throw when apellido is empty")
    void createPropietario_WhenApellidoEmpty_ShouldThrowException() {
        validUser.setApellido("");
        assertThrows(DomainException.class, () -> userUseCase.createPropietario(validUser));
    }

    @Test
    @DisplayName("Should throw when clave is too short")
    void createPropietario_WhenClaveTooShort_ShouldThrowException() {
        validUser.setClave("123");
        assertThrows(DomainException.class, () -> userUseCase.createPropietario(validUser));
    }

    @Test
    @DisplayName("Should throw when fecha nacimiento is null")
    void createPropietario_WhenFechaNacimientoNull_ShouldThrowException() {
        validUser.setFechaNacimiento(null);
        assertThrows(DomainException.class, () -> userUseCase.createPropietario(validUser));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_WhenExists_ShouldReturnUser() {
        User existingUser = User.builder().id(1L).nombre("Juan").build();
        when(userPersistencePort.findUserById(1L)).thenReturn(Optional.of(existingUser));

        User result = userUseCase.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void getUserById_WhenNotFound_ShouldThrowException() {
        when(userPersistencePort.findUserById(999L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userUseCase.getUserById(999L));
    }

    @Test
    @DisplayName("Should get user by correo")
    void getUserByCorreo_ShouldReturnOptional() {
        User user = User.builder().id(1L).correo("test@test.com").build();
        when(userPersistencePort.findUserByCorreo("test@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userUseCase.getUserByCorreo("test@test.com");

        assertTrue(result.isPresent());
        assertEquals("test@test.com", result.get().getCorreo());
    }

    @Test
    @DisplayName("Should check if correo exists")
    void existsByCorreo_ShouldDelegateToPort() {
        when(userPersistencePort.existsByCorreo("test@test.com")).thenReturn(true);
        assertTrue(userUseCase.existsByCorreo("test@test.com"));
    }

    @Test
    @DisplayName("Should check if documento exists")
    void existsByDocumento_ShouldDelegateToPort() {
        when(userPersistencePort.existsByDocumentoIdentidad("123")).thenReturn(false);
        assertFalse(userUseCase.existsByDocumentoIdentidad("123"));
    }
}