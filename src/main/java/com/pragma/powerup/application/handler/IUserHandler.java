package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;

public interface IUserHandler {
    UserResponseDto createPropietario(UserRequestDto requestDto);
    UserResponseDto createEmpleado(UserRequestDto requestDto);
    UserResponseDto createCliente(UserRequestDto requestDto);
    UserResponseDto getUserById(Long id);
}
