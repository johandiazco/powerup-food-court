package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Dish;
import java.util.List;

public interface IDishServicePort {
    Dish createDish(Dish dish);
    Dish getDishById(Long id);
    List<Dish> getDishesByRestaurant(Long restaurantId);
}
