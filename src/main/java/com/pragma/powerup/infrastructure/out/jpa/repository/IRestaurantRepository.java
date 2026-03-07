package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.RestauranteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IRestauranteRepository extends JpaRepository<RestauranteEntity, Long> {
    Optional<RestauranteEntity> findByNit(String nit);
    boolean existsByNit(String nit);
}
