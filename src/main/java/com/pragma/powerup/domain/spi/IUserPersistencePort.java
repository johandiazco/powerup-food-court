package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.User;
import java.util.Optional;

public interface IUserPersistencePort {
    User saveUser(User user);
    Optional<User> findUserById(Long id);
    Optional<User> findUserByCorreo(String correo);
    Optional<User> findUserByDocumentoIdentidad(String documentoIdentidad);
    boolean existsByCorreo(String correo);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}