package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.UpdateDishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.application.mapper.IDishResponseMapper;
import com.pragma.powerup.application.mapper.IUpdateDishRequestMapper;
import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.model.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final IUpdateDishRequestMapper updateRequestMapper;

    @Override
    public DishResponseDto createDish(DishRequestDto requestDto) {
        Dish dish = requestMapper.toDish(requestDto);
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

    @Override
    public DishResponseDto updateDish(Long id, UpdateDishRequestDto updateDto) {
        Dish dishUpdates = updateRequestMapper.toDish(updateDto);
        Dish updatedDish = dishServicePort.updateDish(id, dishUpdates);
        return responseMapper.toResponseDto(updatedDish);
    }

    @Override
    public DishResponseDto toggleDishStatus(Long id, Boolean active) {
        Dish updatedDish = dishServicePort.toggleDishStatus(id, active);
        return responseMapper.toResponseDto(updatedDish);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponseDto<DishResponseDto> getActiveDishesPaginated(
            Long restaurantId,
            Long categoryId,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        //Creamos Sort
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        //Creamos Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        //Ejecutamos búsqueda paginada
        Page<Dish> dishPage = dishServicePort.getActiveDishesByRestaurant(restaurantId, categoryId, pageable);

        //Convertimos a DTOs
        List<DishResponseDto> dishDtos = responseMapper.toResponseDtoList(dishPage.getContent());

        //Construimos respuesta paginada
        PageableResponseDto<DishResponseDto> response = new PageableResponseDto<>();
        response.setContent(dishDtos);
        response.setPageNumber(dishPage.getNumber());
        response.setPageSize(dishPage.getSize());
        response.setTotalElements(dishPage.getTotalElements());
        response.setTotalPages(dishPage.getTotalPages());
        response.setFirst(dishPage.isFirst());
        response.setLast(dishPage.isLast());
        response.setEmpty(dishPage.isEmpty());

        return response;
    }
}