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
}