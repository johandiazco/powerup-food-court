package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.DomainPage;
import com.pragma.powerup.domain.model.PaginationParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PaginationMapper {

    private PaginationMapper() {
    }
    public static Pageable toPageable(PaginationParams params) {
        if (params.getSortBy() != null && !params.getSortBy().isEmpty()) {
            Sort.Direction direction = params.isAscending()
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, params.getSortBy());
            return PageRequest.of(params.getPage(), params.getSize(), sort);
        }
        return PageRequest.of(params.getPage(), params.getSize());
    }

    public static <E, D> DomainPage<D> toDomainPage(Page<E> springPage, Function<E, D> mapper) {
        return new DomainPage<>(
                springPage.getContent().stream()
                        .map(mapper)
                        .collect(Collectors.toList()),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements()
        );
    }
}