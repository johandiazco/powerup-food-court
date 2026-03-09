package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Restaurant;
import java.util.Optional;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);
    Optional<Restaurant> findRestaurantById(Long id);
    Optional<Restaurant> findRestaurantByNit(String nit);
    boolean existsByNit(String nit);
}
