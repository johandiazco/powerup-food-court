package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {
    List<DishEntity> findByRestauranteIdAndActivoTrue(Long restauranteId);
    @Query("SELECT p FROM DishEntity p WHERE p.restaurant.id = :restauranteId " +
            "AND p.category.id = :categoriaId AND p.active = true")
    List<DishEntity> findByRestauranteAndCategoria(
            @Param("restaurantId") Long restaurantId,
            @Param("categoryId") Long categoryId
    );
}
