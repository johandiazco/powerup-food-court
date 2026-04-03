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
        verificarDatosPlato(dish);
        verificarRestauranteExiste(dish.getRestaurantId());
        verificarCategoriaExiste(dish.getCategoryId());

        if (dish.getOwnerId() != null) {
            verificarPropiedadRestaurante(dish.getRestaurantId(), dish.getOwnerId());
        }

        if (dish.getActive() == null) {
            dish.setActive(true);
        }

        return dishPersistencePort.saveDish(dish);
    }

    @Override
    public Dish getDishById(Long id) {
        return buscarPlatoOLanzar(id);
    }

    @Override
    public List<Dish> getDishesByRestaurant(Long restaurantId) {
        verificarRestauranteExiste(restaurantId);
        return dishPersistencePort.findDishesByRestaurantId(restaurantId);
    }

    @Override
    public Dish updateDish(Long id, Dish dishUpdates) {
        Dish existingDish = buscarPlatoOLanzar(id);

        actualizarPrecio(existingDish, dishUpdates.getPrice());
        actualizarDescripcion(existingDish, dishUpdates.getDescription());

        return dishPersistencePort.updateDish(existingDish);
    }

    @Override
    public Dish toggleDishStatus(Long id, Boolean active) {
        Dish existingDish = buscarPlatoOLanzar(id);
        existingDish.setActive(active);
        return dishPersistencePort.updateDish(existingDish);
    }

    @Override
    public DomainPage<Dish> getActiveDishesByRestaurant(Long restaurantId, Long categoryId, PaginationParams params) {
        verificarRestauranteExiste(restaurantId);

        if (categoryId != null) {
            verificarCategoriaExiste(categoryId);
        }

        return dishPersistencePort.findActiveDishesByRestaurant(restaurantId, categoryId, params);
    }

    private Dish buscarPlatoOLanzar(Long id) {
        return dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(id));
    }

    private void verificarRestauranteExiste(Long restaurantId) {
        restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }

    private void verificarCategoriaExiste(Long categoryId) {
        if (!categoryPersistencePort.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
    }

    private void verificarPropiedadRestaurante(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedAccessException("El usuario no es propietario de este restaurante");
        }
    }

    private void verificarDatosPlato(Dish dish) {
        if (dish.getName() == null || dish.getName().trim().isEmpty()) {
            throw new DomainException("El nombre del plato no puede estar vacío");
        }
        if (dish.getName().length() > 100) {
            throw new DomainException("El nombre del plato no puede exceder 100 caracteres");
        }
        if (dish.getPrice() == null || dish.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("El precio debe ser mayor a cero");
        }
        if (dish.getDescription() == null || dish.getDescription().trim().isEmpty()) {
            throw new DomainException("La descripción del plato no puede estar vacía");
        }
        if (dish.getDescription().length() > 500) {
            throw new DomainException("La descripción no puede exceder 500 caracteres");
        }
        if (dish.getImageUrl() == null || dish.getImageUrl().trim().isEmpty()) {
            throw new DomainException("La URL de la imagen no puede estar vacía");
        }
        if (!dish.getImageUrl().matches("^https?://.*")) {
            throw new DomainException("La URL de la imagen debe comenzar con http:// o https://");
        }
        if (dish.getCategoryId() == null) {
            throw new DomainException("La categoría es obligatoria");
        }
        if (dish.getRestaurantId() == null) {
            throw new DomainException("El restaurante es obligatorio");
        }
    }

    private void actualizarPrecio(Dish existingDish, BigDecimal newPrice) {
        if (newPrice == null) {
            return;
        }
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("El precio debe ser mayor a cero");
        }
        existingDish.setPrice(newPrice);
    }

    private void actualizarDescripcion(Dish existingDish, String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            return;
        }
        if (newDescription.length() > 500) {
            throw new DomainException("La descripción no puede exceder 500 caracteres");
        }
        existingDish.setDescription(newDescription);
    }
}