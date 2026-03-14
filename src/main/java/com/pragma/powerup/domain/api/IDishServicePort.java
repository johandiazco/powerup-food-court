package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IDishServicePort {
    Dish createDish(Dish dish);
    Dish getDishById(Long id);
    List<Dish> getDishesByRestaurant(Long restaurantId);
    Dish updateDish(Long id, Dish dish);
    Dish toggleDishStatus(Long id, Boolean active);
    Page<Dish> getActiveDishesByRestaurant(Long restaurantId, Long categoryId, Pageable pageable);
}