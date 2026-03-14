package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.UpdateDishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;
import java.util.List;

public interface IDishHandler {
    DishResponseDto createDish(DishRequestDto requestDto);
    DishResponseDto getDishById(Long id);
    List<DishResponseDto> getDishesByRestaurant(Long restaurantId);
    DishResponseDto updateDish(Long id, UpdateDishRequestDto updateDto);
    DishResponseDto toggleDishStatus(Long id, Boolean active);
    PageableResponseDto<DishResponseDto> getActiveDishesPaginated(
            Long restaurantId,
            Long categoryId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
}