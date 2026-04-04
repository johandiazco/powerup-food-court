package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.exception.UserAlreadyExistsException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

import java.time.LocalDate;
import java.util.Optional;

public class UserUseCase implements IUserServicePort {

    private static final String FORMATO_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String FORMATO_CELULAR = "^\\+?\\d{10,13}$";
    private static final String FORMATO_DOCUMENTO = "^\\d+$";

    private final IUserPersistencePort userPersistencePort;

    public UserUseCase(IUserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public User createPropietario(User user) {
        user.setRol(Role.PROPIETARIO);
        verificarDatosObligatorios(user);
        verificarFormatos(user);
        verificarMayoriaDeEdad(user);
        verificarNoExisteUsuario(user);

        return userPersistencePort.saveUser(user);
    }

    @Override
    public User createEmpleado(User user) {
        user.setRol(Role.EMPLEADO);
        verificarDatosObligatorios(user);
        verificarFormatos(user);
        verificarNoExisteUsuario(user);

        return userPersistencePort.saveUser(user);
    }

    @Override
    public User createCliente(User user) {
        user.setRol(Role.CLIENTE);
        verificarDatosObligatorios(user);
        verificarFormatos(user);
        verificarNoExisteUsuario(user);

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

    private void verificarDatosObligatorios(User user) {
        verificarDatosPersonales(user);
        verificarDatosContacto(user);
        verificarDatosSeguridad(user);
    }

    private void verificarDatosPersonales(User user) {
        if (user.getNombre() == null || user.getNombre().trim().isEmpty()) {
            throw new DomainException("El nombre es obligatorio");
        }
        if (user.getNombre().length() > 100) {
            throw new DomainException("El nombre no puede exceder 100 caracteres");
        }
        if (user.getApellido() == null || user.getApellido().trim().isEmpty()) {
            throw new DomainException("El apellido es obligatorio");
        }
        if (user.getApellido().length() > 100) {
            throw new DomainException("El apellido no puede exceder 100 caracteres");
        }
        if (user.getDocumentoIdentidad() == null || user.getDocumentoIdentidad().trim().isEmpty()) {
            throw new DomainException("El documento de identidad es obligatorio");
        }
    }

    private void verificarDatosContacto(User user) {
        if (user.getCelular() == null || user.getCelular().trim().isEmpty()) {
            throw new DomainException("El celular es obligatorio");
        }
        if (user.getFechaNacimiento() == null) {
            throw new DomainException("La fecha de nacimiento es obligatoria");
        }
        if (user.getCorreo() == null || user.getCorreo().trim().isEmpty()) {
            throw new DomainException("El correo es obligatorio");
        }
    }

    private void verificarDatosSeguridad(User user) {
        if (user.getClave() == null || user.getClave().trim().isEmpty()) {
            throw new DomainException("La clave es obligatoria");
        }
        if (user.getClave().length() < 6) {
            throw new DomainException("La clave debe tener al menos 6 caracteres");
        }
        if (user.getRol() == null) {
            throw new DomainException("El rol es obligatorio");
        }
    }

    private void verificarFormatos(User user) {
        verificarFormatoEmail(user.getCorreo());
        verificarFormatoCelular(user.getCelular());
        verificarFormatoDocumento(user.getDocumentoIdentidad());
    }

    private void verificarFormatoEmail(String correo) {
        if (!correo.matches(FORMATO_EMAIL)) {
            throw new DomainException("El formato del correo electrónico no es válido");
        }
        if (correo.length() > 100) {
            throw new DomainException("El correo no puede exceder 100 caracteres");
        }
    }

    private void verificarFormatoCelular(String celular) {
        if (!celular.matches(FORMATO_CELULAR)) {
            throw new DomainException("El celular debe contener máximo 13 caracteres y puede contener el símbolo +");
        }
    }

    private void verificarFormatoDocumento(String documento) {
        if (!documento.matches(FORMATO_DOCUMENTO)) {
            throw new DomainException("El documento de identidad debe ser únicamente numérico");
        }
        if (documento.length() > 20) {
            throw new DomainException("El documento de identidad no puede exceder 20 caracteres");
        }
    }

    private void verificarMayoriaDeEdad(User user) {
        if (user.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new DomainException("La fecha de nacimiento no puede ser futura");
        }
        if (!user.esMayorDeEdad()) {
            throw new DomainException("El usuario debe ser mayor de edad (18 años o más)");
        }
    }

    private void verificarNoExisteUsuario(User user) {
        verificarCorreoNoRegistrado(user.getCorreo());
        verificarDocumentoNoRegistrado(user.getDocumentoIdentidad());
    }

    private void verificarCorreoNoRegistrado(String correo) {
        if (userPersistencePort.existsByCorreo(correo)) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el correo: " + correo);
        }
    }

    private void verificarDocumentoNoRegistrado(String documento) {
        if (userPersistencePort.existsByDocumentoIdentidad(documento)) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el documento de identidad: " + documento);
        }
    }
}