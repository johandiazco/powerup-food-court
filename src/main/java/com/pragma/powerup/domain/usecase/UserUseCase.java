package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
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
        //Validamoa modelo
        user.validate();

        //Se verifica correo no duplicado
        if (userPersistencePort.existsByCorreo(user.getCorreo())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el correo: " + user.getCorreo());
        }

        //Se verificar documento que no este duplicado
        if (userPersistencePort.existsByDocumentoIdentidad(user.getDocumentoIdentidad())) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el documento de identidad: " + user.getDocumentoIdentidad()
            );
        }

        //Asignamos rol PROPIETARIO
        user.setRol(Role.PROPIETARIO);

        //Guardamos usuario. !la clave ya viene encriptada desde el handler!
        return userPersistencePort.saveUser(user);
    }

    @Override
    public User createEmpleado(User user) {
        //Validamos modelo
        user.validate();

        //Se verificar duplicados
        if (userPersistencePort.existsByCorreo(user.getCorreo())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el correo: " + user.getCorreo());
        }

        if (userPersistencePort.existsByDocumentoIdentidad(user.getDocumentoIdentidad())) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el documento de identidad: " + user.getDocumentoIdentidad()
            );
        }

        //Asignamos rol EMPLEADO
        user.setRol(Role.EMPLEADO);

        return userPersistencePort.saveUser(user);
    }

    @Override
    public User createCliente(User user) {
        //Validamos modelo
        user.validate();

        //Se verifican duplicados
        if (userPersistencePort.existsByCorreo(user.getCorreo())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el correo: " + user.getCorreo());
        }

        if (userPersistencePort.existsByDocumentoIdentidad(user.getDocumentoIdentidad())) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el documento de identidad: " + user.getDocumentoIdentidad()
            );
        }

        //Asignamos rol CLIENTE
        user.setRol(Role.CLIENTE);

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
}