package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IAuthenticationService;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.AuthResult;
import com.pragma.powerup.domain.model.LoginCommand;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IAuthenticationPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

public class AuthenticationUseCase implements IAuthenticationService {

    private final IAuthenticationPort authenticationPort;
    private final IUserPersistencePort userPersistencePort;

    public AuthenticationUseCase(
            IAuthenticationPort authenticationPort,
            IUserPersistencePort userPersistencePort) {
        this.authenticationPort = authenticationPort;
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public AuthResult login(LoginCommand loginCommand) {
        // Validación de entrada en el UseCase (no en el modelo)
        validateLoginInput(loginCommand);

        // Autenticamos las credenciales a través del puerto
        authenticationPort.authenticate(loginCommand.getCorreo(), loginCommand.getClave());

        // Obtenemos datos del usuario desde persistencia
        User user = userPersistencePort.findUserByCorreo(loginCommand.getCorreo())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));

        // Generamos token a través del puerto
        return authenticationPort.generateToken(
                user.getCorreo(),
                user.getId(),
                user.getRol().name()
        );
    }

    private void validateLoginInput(LoginCommand loginCommand) {
        if (loginCommand.getCorreo() == null || loginCommand.getCorreo().trim().isEmpty()) {
            throw new DomainException("El correo es obligatorio para iniciar sesión");
        }
        if (loginCommand.getClave() == null || loginCommand.getClave().trim().isEmpty()) {
            throw new DomainException("La clave es obligatoria para iniciar sesión");
        }
    }
}