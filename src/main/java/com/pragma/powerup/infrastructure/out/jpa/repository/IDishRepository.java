package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {

    List<DishEntity> findByRestaurantIdAndActiveTrue(Long restaurantId);

    @Query("SELECT d FROM DishEntity d WHERE d.restaurant.id = :restaurantId " +
            "AND d.category.id = :categoryId AND d.active = true")
    List<DishEntity> findByRestaurantAndCategory(
            @Param("restaurantId") Long restaurantId,
            @Param("categoryId") Long categoryId
    );
}