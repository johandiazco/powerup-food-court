package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        //Validamos datos del restaurante
        restaurant.validate();

        //Verificamos que el NIT no esté duplicado
        if (restaurantPersistencePort.existsByNit(restaurant.getNit())) {
            throw new RestaurantAlreadyExistsException(restaurant.getNit());
        }

        //Guardamos el restaurante
        return restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantPersistencePort.findRestaurantById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }

    @Override
    public boolean existsByNit(String nit) {
        return restaurantPersistencePort.existsByNit(nit);
    }

    @Override
    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return restaurantPersistencePort.findAllRestaurants(pageable);
    }
}