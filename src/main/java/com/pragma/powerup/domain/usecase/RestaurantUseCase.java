package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.exception.RestaurantAlreadyExistsException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.PaginationParams;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.model.Role;
import com.pragma.powerup.domain.model.User;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserPersistencePort userPersistencePort;

    public RestaurantUseCase(
            IRestaurantPersistencePort restaurantPersistencePort,
            IUserPersistencePort userPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.validate();

        validateOwnerRole(restaurant.getOwnerId());

        if (restaurantPersistencePort.existsByNit(restaurant.getNit())) {
            throw new RestaurantAlreadyExistsException(restaurant.getNit());
        }

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
    public DomainPage<Restaurant> getAllRestaurants(PaginationParams params) {
        return restaurantPersistencePort.findAllRestaurants(params);
    }

    private void validateOwnerRole(Long ownerId) {
        User owner = userPersistencePort.findUserById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        if (owner.getRol() != Role.PROPIETARIO) {
            throw new DomainException(
                    "El usuario con ID " + ownerId + " no tiene rol de PROPIETARIO");
        }
    }
}