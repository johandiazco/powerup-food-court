package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.AuthResult;
import com.pragma.powerup.domain.model.LoginCommand;

public interface IAuthenticationService {
    AuthResult login(LoginCommand loginCommand);
}