package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.exception.UserAlreadyExistsException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

import java.util.Optional;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;

    public UserUseCase(IUserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public User createPropietario(User user) {
        // Validamos modelo
        user.setRol(Role.PROPIETARIO);
        user.validate();

        // Validamos mayoría de edad explícitamente
        validateMayorDeEdad(user);

        // Verificamos duplicados
        validateNoDuplicates(user);

        // La clave ya viene encriptada desde el handler
        return userPersistencePort.saveUser(user);
    }

    @Override
    public User createEmpleado(User user) {
        // Validamos modelo
        user.setRol(Role.EMPLEADO);
        user.validate();

        // Verificamos duplicados
        validateNoDuplicates(user);

        return userPersistencePort.saveUser(user);
    }

    @Override
    public User createCliente(User user) {
        // Validamos modelo
        user.setRol(Role.CLIENTE);
        user.validate();

        // Verificamos duplicados
        validateNoDuplicates(user);

        return userPersistencePort.saveUser(user);
    }

    @Override
    public User getUserById(Long id) {
        return userPersistencePort.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public Optional<User> getUserByCorreo(String correo) {
        return userPersistencePort.findUserByCorreo(correo);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return userPersistencePort.existsByCorreo(correo);
    }

    @Override
    public boolean existsByDocumentoIdentidad(String documentoIdentidad) {
        return userPersistencePort.existsByDocumentoIdentidad(documentoIdentidad);
    }

    private void validateMayorDeEdad(User user) {
        if (!user.isMayorDeEdad()) {
            throw new DomainException("El usuario debe ser mayor de edad (18 años o más)");
        }
    }

    private void validateNoDuplicates(User user) {
        if (userPersistencePort.existsByCorreo(user.getCorreo())) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el correo: " + user.getCorreo());
        }

        if (userPersistencePort.existsByDocumentoIdentidad(user.getDocumentoIdentidad())) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el documento de identidad: " + user.getDocumentoIdentidad());
        }
    }
}