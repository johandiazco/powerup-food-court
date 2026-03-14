package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.Dish;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public Dish saveDish(Dish dish) {
        DishEntity entity = dishEntityMapper.toEntity(dish);
        DishEntity savedEntity = dishRepository.save(entity);
        return dishEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Dish> findDishById(Long id) {
        return dishRepository.findById(id)
                .map(dishEntityMapper::toDomain);
    }

    @Override
    public List<Dish> findDishesByRestaurantId(Long restaurantId) {
        List<DishEntity> entities = dishRepository.findByRestaurantId(restaurantId);
        return entities.stream()
                .map(dishEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return dishRepository.existsById(id);
    }

    @Override
    public Dish updateDish(Dish dish) {
        DishEntity entity = dishEntityMapper.toEntity(dish);
        DishEntity updatedEntity = dishRepository.save(entity);
        return dishEntityMapper.toDomain(updatedEntity);
    }

    @Override
    public Page<Dish> findActiveDishesByRestaurant(Long restaurantId, Long categoryId, Pageable pageable) {
        Page<DishEntity> entityPage;

        if (categoryId != null) {
            //Filtramos por restaurante, categoría y solo activos
            entityPage = dishRepository.findByRestaurantIdAndCategoryIdAndActiveTrue(
                    restaurantId, categoryId, pageable);
        } else {
            //Filtramos solo por restaurante y activos
            entityPage = dishRepository.findByRestaurantIdAndActiveTrue(restaurantId, pageable);
        }

        //Convertimos Page<DishEntity> a Page<Dish>
        return entityPage.map(dishEntityMapper::toDomain);
    }
}