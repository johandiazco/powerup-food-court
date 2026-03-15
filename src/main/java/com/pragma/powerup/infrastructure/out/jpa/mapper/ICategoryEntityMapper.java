package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.Category;
import com.pragma.powerup.infrastructure.out.jpa.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ICategoryEntityMapper {

    @Mapping(target = "createdAt", ignore = true)
    CategoryEntity toEntity(Category category);
    Category toDomain(CategoryEntity entity);
    List<Category> toDomainList(List<CategoryEntity> entities);
}