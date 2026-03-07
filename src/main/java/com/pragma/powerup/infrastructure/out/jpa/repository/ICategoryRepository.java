package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ICategoriaRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
