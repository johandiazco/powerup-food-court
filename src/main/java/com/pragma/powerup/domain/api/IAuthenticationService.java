package com.pragma.powerup.domain.api;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;

public interface IAuthenticationService {
    AuthResponseDto login(LoginRequestDto loginRequest);
}