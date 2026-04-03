package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.PaginationParams;

import java.util.List;

public interface IDishServicePort {
    Dish createDish(Dish dish);
    Dish getDishById(Long id);
    List<Dish> getDishesByRestaurant(Long restaurantId);
    Dish updateDish(Long id, Dish dish);
    Dish toggleDishStatus(Long id, Boolean active);
    DomainPage<Dish> getActiveDishesByRestaurant(Long restaurantId, Long categoryId, PaginationParams params);
}