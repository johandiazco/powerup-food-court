package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHandler implements IUserHandler {

    private final IUserServicePort userServicePort;
    private final IUserRequestMapper requestMapper;
    private final IUserResponseMapper responseMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createPropietario(UserRequestDto requestDto) {
        User user = requestMapper.toUser(requestDto);

        //Encriptamos contraseña con BCrypt
        user.setClave(passwordEncoder.encode(requestDto.getClave()));

        User createdUser = userServicePort.createPropietario(user);
        return responseMapper.toResponseDto(createdUser);
    }

    @Override
    public UserResponseDto createEmpleado(UserRequestDto requestDto) {
        User user = requestMapper.toUser(requestDto);

        //Encriptamos contraseña con BCrypt
        user.setClave(passwordEncoder.encode(requestDto.getClave()));

        User createdUser = userServicePort.createEmpleado(user);
        return responseMapper.toResponseDto(createdUser);
    }

    @Override
    public UserResponseDto createCliente(UserRequestDto requestDto) {
        User user = requestMapper.toUser(requestDto);

        //Encriptamos contraseña con BCrypt
        user.setClave(passwordEncoder.encode(requestDto.getClave()));

        User createdUser = userServicePort.createCliente(user);
        return responseMapper.toResponseDto(createdUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userServicePort.getUserById(id);
        return responseMapper.toResponseDto(user);
    }
}