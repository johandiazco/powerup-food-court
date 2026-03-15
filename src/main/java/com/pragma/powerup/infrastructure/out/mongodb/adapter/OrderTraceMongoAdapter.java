package com.pragma.powerup.infrastructure.out.mongodb.adapter;

import com.pragma.powerup.domain.model.OrderTrace;
import com.pragma.powerup.domain.spi.IOrderTracePersistencePort;
import com.pragma.powerup.infrastructure.out.mongodb.entity.OrderTraceDocument;
import com.pragma.powerup.infrastructure.out.mongodb.mapper.IOrderTraceDocumentMapper;
import com.pragma.powerup.infrastructure.out.mongodb.repository.IOrderTraceMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTraceMongoAdapter implements IOrderTracePersistencePort {

    private final IOrderTraceMongoRepository repository;
    private final IOrderTraceDocumentMapper mapper;

    @Override
    public OrderTrace saveTrace(OrderTrace trace) {
        OrderTraceDocument document = mapper.toDocument(trace);
        OrderTraceDocument savedDocument = repository.save(document);

        log.debug("Trazabilidad guardada en MongoDB con ID: {}", savedDocument.getId());

        return mapper.toDomain(savedDocument);
    }

    @Override
    public List<OrderTrace> findTracesByOrderId(Long orderId) {
        List<OrderTraceDocument> documents =
                repository.findByOrderIdOrderByChangeDateAsc(orderId);

        return mapper.toDomainList(documents);
    }

    @Override
    public List<OrderTrace> findAllTraces() {
        List<OrderTraceDocument> documents = repository.findAll();
        return mapper.toDomainList(documents);
    }
}