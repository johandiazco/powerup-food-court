package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        //validamos datos del restaurante
        restaurant.validate();

        //se verificar que el NIT/NIE no esté duplicado
        if (restaurantPersistencePort.existsByNit(restaurant.getNit())) {
            throw new RestaurantAlreadyExistsException(restaurant.getNit());
        }

        //se guardar el restaurante
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
}
