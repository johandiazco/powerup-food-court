package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.User;
import java.util.Optional;

public interface IUserServicePort {
    User createPropietario(User user);
    User createEmpleado(User user);
    User createCliente(User user);
    User getUserById(Long id);
    Optional<User> getUserByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}