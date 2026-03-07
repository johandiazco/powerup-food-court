package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.DisheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IDisheRepository extends JpaRepository<DisheEntity, Long> {
    List<DisheEntity> findByRestauranteIdAndActivoTrue(Long restauranteId);
    @Query("SELECT p FROM DisheEntity p WHERE p.restaurant.id = :restauranteId " +
            "AND p.category.id = :categoriaId AND p.active = true")
    List<DisheEntity> findByRestauranteAndCategoria(
            @Param("restaurantId") Long restaurantId,
            @Param("categoryId") Long categoryId
    );
}
