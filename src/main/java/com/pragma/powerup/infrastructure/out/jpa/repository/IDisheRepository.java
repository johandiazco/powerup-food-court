package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.PlateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IPlatoRepository extends JpaRepository<PlateEntity, Long> {
    List<PlateEntity> findByRestauranteIdAndActivoTrue(Long restauranteId);
    @Query("SELECT p FROM PlateEntity p WHERE p.restaurante.id = :restauranteId " +
            "AND p.categoria.id = :categoriaId AND p.activo = true")
    List<PlateEntity> findByRestauranteAndCategoria(
            @Param("restauranteId") Long restauranteId,
            @Param("categoriaId") Long categoriaId
    );
}
