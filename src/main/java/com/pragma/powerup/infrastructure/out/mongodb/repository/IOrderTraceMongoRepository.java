package com.pragma.powerup.infrastructure.out.mongodb.repository;

import com.pragma.powerup.infrastructure.out.mongodb.entity.OrderTraceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IOrderTraceMongoRepository extends MongoRepository<OrderTraceDocument, String> {
    List<OrderTraceDocument> findByOrderIdOrderByChangeDateAsc(Long orderId);
}