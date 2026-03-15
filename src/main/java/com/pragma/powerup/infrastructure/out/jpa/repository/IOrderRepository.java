package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByRestaurantIdAndStatus(
            Long restaurantId,
            OrderStatusEntity status,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM OrderEntity o " +
            "WHERE o.clientId = :clientId " +
            "AND o.restaurantId = :restaurantId " +
            "AND o.status NOT IN ('ENTREGADO', 'CANCELADO')")
    boolean existsActiveOrderByClientIdAndRestaurantId(
            @Param("clientId") Long clientId,
            @Param("restaurantId") Long restaurantId);
}