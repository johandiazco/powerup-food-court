package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.application.mapper.IDishResponseMapper;
import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper requestMapper;
    private final IDishResponseMapper responseMapper;

    @Override
    public DishResponseDto createDish(DishRequestDto requestDto) {
        Dish dish = requestMapper.toDish(requestDto);
        //ejecutamos logica de dominio
        Dish createdDish = dishServicePort.createDish(dish);
        return responseMapper.toResponseDto(createdDish);
    }

    @Override
    @Transactional(readOnly = true)
    public DishResponseDto getDishById(Long id) {
        Dish dish = dishServicePort.getDishById(id);
        return responseMapper.toResponseDto(dish);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishResponseDto> getDishesByRestaurant(Long restaurantId) {
        List<Dish> dishes = dishServicePort.getDishesByRestaurant(restaurantId);
        return responseMapper.toResponseDtoList(dishes);
    }
}