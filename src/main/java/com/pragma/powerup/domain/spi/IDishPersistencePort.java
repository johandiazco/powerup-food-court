package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.Dish;
import java.util.List;
import java.util.Optional;

public interface IDishPersistencePort {
    Dish saveDish(Dish dish);
    Optional<Dish> findDishById(Long id);
    List<Dish> findDishesByRestaurantId(Long restaurantId);
    boolean existsById(Long id);
    Dish updateDish(Dish dish);
}