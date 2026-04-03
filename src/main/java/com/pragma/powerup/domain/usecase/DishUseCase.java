package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.exception.CategoryNotFoundException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.exception.UnauthorizedAccessException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.PaginationParams;
import com.pragma.powerup.domain.model.Restaurant;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.math.BigDecimal;
import java.util.List;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;

    public DishUseCase(
            IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            ICategoryPersistencePort categoryPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    public Dish createDish(Dish dish) {
        dish.validate();
        validateRestaurantExists(dish.getRestaurantId());
        validateCategoryExists(dish.getCategoryId());

        if (dish.getOwnerId() != null) {
            validateOwnership(dish.getRestaurantId(), dish.getOwnerId());
        }

        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish getDishById(Long id) {
        return findDishOrThrow(id);
    }

    @Override
    public List<Dish> getDishesByRestaurant(Long restaurantId) {
        validateRestaurantExists(restaurantId);
        return dishPersistencePort.findDishesByRestaurantId(restaurantId);
    }

    @Override
    public Dish updateDish(Long id, Dish dishUpdates) {
        Dish existingDish = findDishOrThrow(id);

        updatePrice(existingDish, dishUpdates.getPrice());
        updateDescription(existingDish, dishUpdates.getDescription());

        return dishPersistencePort.updateDish(existingDish);
    }

    @Override
    public Dish toggleDishStatus(Long id, Boolean active) {
        Dish existingDish = findDishOrThrow(id);
        existingDish.setActive(active);
        return dishPersistencePort.updateDish(existingDish);
    }

    @Override
    public DomainPage<Dish> getActiveDishesByRestaurant(Long restaurantId, Long categoryId, PaginationParams params) {
        validateRestaurantExists(restaurantId);

        if (categoryId != null) {
            validateCategoryExists(categoryId);
        }

        return dishPersistencePort.findActiveDishesByRestaurant(restaurantId, categoryId, params);
    }

    private Dish findDishOrThrow(Long id) {
        return dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
    }

    private void validateRestaurantExists(Long restaurantId) {
        restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private void validateCategoryExists(Long categoryId) {
        if (!categoryPersistencePort.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
    }

    private void validateOwnership(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedAccessException(
                    "El usuario no es propietario de este restaurante");
        }
    }

    private void updatePrice(Dish existingDish, BigDecimal newPrice) {
        if (newPrice == null) {
            return;
        }
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        existingDish.setPrice(newPrice);
    }

    private void updateDescription(Dish existingDish, String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            return;
        }
        if (newDescription.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
        }
        existingDish.setDescription(newDescription);
    }
}