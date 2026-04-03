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
        // Validación de negocio en el UseCase
        validateRestaurantData(restaurant);
        validateOwnerRole(restaurant.getOwnerId());

        // Verificamos NIT no duplicado
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

    private void validateRestaurantData(Restaurant restaurant) {
        if (restaurant.getName() == null || restaurant.getName().trim().isEmpty()) {
            throw new DomainException("El nombre del restaurante es obligatorio");
        }

        if (!restaurant.getName().matches("^(?=.*[a-zA-Z]).+$")) {
            throw new DomainException("El nombre del restaurante debe contener al menos una letra");
        }

        if (restaurant.getNit() == null || restaurant.getNit().length() < 5) {
            throw new DomainException("El NIT debe tener al menos 5 caracteres");
        }

        if (restaurant.getAddress() == null || restaurant.getAddress().trim().isEmpty()) {
            throw new DomainException("La dirección es obligatoria");
        }

        if (restaurant.getPhone() == null || !restaurant.getPhone().matches("^\\+?\\d{10,13}$")) {
            throw new DomainException(
                    "El teléfono debe tener entre 10 y 13 dígitos (puede incluir +)");
        }

        if (restaurant.getOwnerId() == null || restaurant.getOwnerId() <= 0) {
            throw new DomainException("El ID del propietario es inválido");
        }
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