package com.pragma.powerup.infrastructure.out.mongodb.mapper;

import com.pragma.powerup.domain.model.OrderTrace;
import com.pragma.powerup.infrastructure.out.mongodb.entity.OrderTraceDocument;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IOrderTraceDocumentMapper {
    OrderTraceDocument toDocument(OrderTrace trace);
    OrderTrace toDomain(OrderTraceDocument document);
    List<OrderTrace> toDomainList(List<OrderTraceDocument> documents);
}