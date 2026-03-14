package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PageableResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantListResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.mapper.IRestaurantListResponseMapper;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.Restaurant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IRestaurantListResponseMapper restaurantListResponseMapper;

    @Override
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        Restaurant restaurant = restaurantRequestMapper.toRestaurant(restaurantRequestDto);
        Restaurant createdRestaurant = restaurantServicePort.createRestaurant(restaurant);
        return restaurantResponseMapper.toResponseDto(createdRestaurant);
    }

    @Override
    public RestaurantResponseDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantServicePort.getRestaurantById(id);
        return restaurantResponseMapper.toResponseDto(restaurant);
    }

    @Override
    public PageableResponseDto<RestaurantListResponseDto> getAllRestaurants(int page, int size) {
        //Creamos Pageable con ordenamiento alfabético por nombre
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        //Ejecutamos búsqueda paginada
        Page<Restaurant> restaurantPage = restaurantServicePort.getAllRestaurants(pageable);

        //Convertimos a DTOs "solo nombre y logo - según HU-9"
        List<RestaurantListResponseDto> restaurantDtos =
                restaurantListResponseMapper.toListResponseDtoList(restaurantPage.getContent());

        //Construimos respuesta paginada
        PageableResponseDto<RestaurantListResponseDto> response = new PageableResponseDto<>();
        response.setContent(restaurantDtos);
        response.setPageNumber(restaurantPage.getNumber());
        response.setPageSize(restaurantPage.getSize());
        response.setTotalElements(restaurantPage.getTotalElements());
        response.setTotalPages(restaurantPage.getTotalPages());
        response.setFirst(restaurantPage.isFirst());
        response.setLast(restaurantPage.isLast());
        response.setEmpty(restaurantPage.isEmpty());

        return response;
    }
}