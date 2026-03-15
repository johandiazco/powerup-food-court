package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.UserAlreadyExistsException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Use Case Tests")
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documentoIdentidad("1234567890")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .correo("juan.perez@example.com")
                .clave("$2a$10$encodedPassword")
                .build();
    }

    //CREATE PROPIETARIO TESTS
    @Test
    @DisplayName("Should create propietario successfully when data is valid and unique")
    void createPropietario_WhenDataIsValidAndUnique_ShouldCreateSuccessfully() {

        User savedUser = User.builder()
                .id(1L)
                .nombre(validUser.getNombre())
                .apellido(validUser.getApellido())
                .documentoIdentidad(validUser.getDocumentoIdentidad())
                .celular(validUser.getCelular())
                .fechaNacimiento(validUser.getFechaNacimiento())
                .correo(validUser.getCorreo())
                .clave(validUser.getClave())
                .rol(Role.PROPIETARIO)
                .build();

        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(userPersistencePort.saveUser(any(User.class))).thenReturn(savedUser);

        User result = userUseCase.createPropietario(validUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
        assertEquals(Role.PROPIETARIO, result.getRol());

        verify(userPersistencePort, times(1)).existsByCorreo("juan.perez@example.com");
        verify(userPersistencePort, times(1)).existsByDocumentoIdentidad("1234567890");
        verify(userPersistencePort, times(1)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when correo already exists")
    void createPropietario_WhenCorreoAlreadyExists_ShouldThrowException() {

        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userUseCase.createPropietario(validUser)
        );

        assertTrue(exception.getMessage().contains("Ya existe un usuario con el correo"));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when documento already exists")
    void createPropietario_WhenDocumentoAlreadyExists_ShouldThrowException() {

        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad(anyString())).thenReturn(true);
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userUseCase.createPropietario(validUser)
        );

        assertTrue(exception.getMessage().contains("Ya existe un usuario con el documento de identidad"));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user is underage")
    void createPropietario_WhenUserIsUnderage_ShouldThrowException() {

        validUser.setFechaNacimiento(LocalDate.now().minusYears(15)); // 15 años

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userUseCase.createPropietario(validUser)
        );

        assertEquals("El usuario debe ser mayor de edad (18 años o más)", exception.getMessage());
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when documento is not numeric")
    void createPropietario_WhenDocumentoIsNotNumeric_ShouldThrowException() {

        validUser.setDocumentoIdentidad("ABC123");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userUseCase.createPropietario(validUser)
        );

        assertEquals("El documento de identidad debe ser únicamente numérico", exception.getMessage());
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when celular format is invalid")
    void createPropietario_WhenCelularFormatIsInvalid_ShouldThrowException() {

        validUser.setCelular("123"); // Too short

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userUseCase.createPropietario(validUser)
        );

        assertTrue(exception.getMessage().contains("celular"));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when correo format is invalid")
    void createPropietario_WhenCorreoFormatIsInvalid_ShouldThrowException() {

        validUser.setCorreo("invalid-email");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userUseCase.createPropietario(validUser)
        );

        assertTrue(exception.getMessage().contains("correo"));
        verify(userPersistencePort, never()).saveUser(any());
    }

    //CREATE EMPLEADO TESTS
    @Test
    @DisplayName("Should create empleado successfully with EMPLEADO role")
    void createEmpleado_WhenDataIsValid_ShouldCreateWithEmpleadoRole() {

        User savedUser = User.builder()
                .id(2L)
                .nombre(validUser.getNombre())
                .apellido(validUser.getApellido())
                .documentoIdentidad(validUser.getDocumentoIdentidad())
                .celular(validUser.getCelular())
                .fechaNacimiento(validUser.getFechaNacimiento())
                .correo(validUser.getCorreo())
                .clave(validUser.getClave())
                .rol(Role.EMPLEADO)
                .build();

        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(userPersistencePort.saveUser(any(User.class))).thenReturn(savedUser);

        User result = userUseCase.createEmpleado(validUser);

        assertNotNull(result);
        assertEquals(Role.EMPLEADO, result.getRol());
        verify(userPersistencePort, times(1)).saveUser(any(User.class));
    }

    //CREATE CLIENTE TESTS
    @Test
    @DisplayName("Should create cliente successfully with CLIENTE role")
    void createCliente_WhenDataIsValid_ShouldCreateWithClienteRole() {

        User savedUser = User.builder()
                .id(3L)
                .nombre(validUser.getNombre())
                .apellido(validUser.getApellido())
                .documentoIdentidad(validUser.getDocumentoIdentidad())
                .celular(validUser.getCelular())
                .fechaNacimiento(validUser.getFechaNacimiento())
                .correo(validUser.getCorreo())
                .clave(validUser.getClave())
                .rol(Role.CLIENTE)
                .build();

        when(userPersistencePort.existsByCorreo(anyString())).thenReturn(false);
        when(userPersistencePort.existsByDocumentoIdentidad(anyString())).thenReturn(false);
        when(userPersistencePort.saveUser(any(User.class))).thenReturn(savedUser);

        User result = userUseCase.createCliente(validUser);

        assertNotNull(result);
        assertEquals(Role.CLIENTE, result.getRol());
        verify(userPersistencePort, times(1)).saveUser(any(User.class));
    }

    //GET USER BY ID TESTS
    @Test
    @DisplayName("Should get user by ID successfully when user exists")
    void getUserById_WhenUserExists_ShouldReturnUser() {

        validUser.setId(1L);
        when(userPersistencePort.findUserById(1L)).thenReturn(Optional.of(validUser));

        User result = userUseCase.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
        verify(userPersistencePort, times(1)).findUserById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {

        when(userPersistencePort.findUserById(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userUseCase.getUserById(999L)
        );

        assertEquals("No se encontró el usuario con ID: 999", exception.getMessage());
    }

    //EXISTS BY CORREO TESTS
    @Test
    @DisplayName("Should return true when correo exists")
    void existsByCorreo_WhenCorreoExists_ShouldReturnTrue() {

        when(userPersistencePort.existsByCorreo("juan.perez@example.com")).thenReturn(true);

        boolean result = userUseCase.existsByCorreo("juan.perez@example.com");

        assertTrue(result);
        verify(userPersistencePort, times(1)).existsByCorreo("juan.perez@example.com");
    }

    @Test
    @DisplayName("Should return false when correo does not exist")
    void existsByCorreo_WhenCorreoDoesNotExist_ShouldReturnFalse() {

        when(userPersistencePort.existsByCorreo("nonexistent@example.com")).thenReturn(false);

        boolean result = userUseCase.existsByCorreo("nonexistent@example.com");

        assertFalse(result);
        verify(userPersistencePort, times(1)).existsByCorreo("nonexistent@example.com");
    }
}