package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {
    List<DishEntity> findByRestaurantIdAndActiveTrue(Long restaurantId);
    @Query("SELECT p FROM DishEntity p WHERE p.restaurant.id = :restaurantId " +
            "AND p.category.id = :categoryId AND p.active = true")
    List<DishEntity> findByRestaurantAndCategory(
            @Param("restaurantId") Long restaurantId,
            @Param("categoryId") Long categoryId
    );
}
