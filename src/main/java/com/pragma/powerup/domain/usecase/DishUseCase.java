package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.exception.CategoryNotFoundException;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
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
        //Validamos modelo
        dish.validate();

        //verifica restaurante existente
        if (!restaurantPersistencePort.existsByNit(String.valueOf(dish.getRestaurantId()))) {
            //se prueba por Id
            restaurantPersistencePort.findRestaurantById(dish.getRestaurantId())
                    .orElseThrow(() -> new RestaurantNotFoundException(dish.getRestaurantId()));
        }

        //Verifica categoría existente
        if (!categoryPersistencePort.existsById(dish.getCategoryId())) {
            throw new CategoryNotFoundException(dish.getCategoryId());
        }

        //se guarda el plato
        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish getDishById(Long id) {
        return dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
    }

    @Override
    public List<Dish> getDishesByRestaurant(Long restaurantId) {
        //se verifica si el restaurante existe
        restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        return dishPersistencePort.findDishesByRestaurantId(restaurantId);
    }

    @Override
    public Dish updateDish(Long id, Dish dishUpdates) {
        //Verificamos que el plato exista
        Dish existingDish = dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(id));

        //Se actualizan solo campos permitidos "precio y descripción"
        if (dishUpdates.getPrice() != null) {
            if (dishUpdates.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a cero");
            }
            existingDish.setPrice(dishUpdates.getPrice());
        }

        if (dishUpdates.getDescription() != null && !dishUpdates.getDescription().trim().isEmpty()) {
            if (dishUpdates.getDescription().length() > 500) {
                throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
            }
            existingDish.setDescription(dishUpdates.getDescription());
        }

        //se actualizan otros campos opcionales, segun pide el requisito
        if (dishUpdates.getName() != null && !dishUpdates.getName().trim().isEmpty()) {
            if (dishUpdates.getName().length() > 100) {
                throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
            }
            existingDish.setName(dishUpdates.getName());
        }

        if (dishUpdates.getImageUrl() != null && !dishUpdates.getImageUrl().trim().isEmpty()) {
            if (!dishUpdates.getImageUrl().matches("^https?://.*")) {
                throw new IllegalArgumentException("La URL de la imagen debe comenzar con http:// o https://");
            }
            existingDish.setImageUrl(dishUpdates.getImageUrl());
        }

        if (dishUpdates.getCategoryId() != null) {
            if (!categoryPersistencePort.existsById(dishUpdates.getCategoryId())) {
                throw new CategoryNotFoundException(dishUpdates.getCategoryId());
            }
            existingDish.setCategoryId(dishUpdates.getCategoryId());
        }

        return dishPersistencePort.updateDish(existingDish);
    }

    @Override
    public Dish toggleDishStatus(Long id, Boolean active) {
        //Verificamos que el plato exista
        Dish existingDish = dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(id));

        //actualizamos estado
        existingDish.setActive(active);

        return dishPersistencePort.updateDish(existingDish);
    }
}