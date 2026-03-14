package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(Long id);
    boolean existsByNit(String nit);
    Page<Restaurant> getAllRestaurants(Pageable pageable);
}