package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Restaurant;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(Long id);
    boolean existsByNit(String nit);
}