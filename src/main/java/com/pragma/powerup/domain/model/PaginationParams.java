package com.pragma.powerup.domain.model;

public class PaginationParams {

    private final int page;
    private final int size;
    private final String sortBy;
    private final boolean ascending;

    public PaginationParams(int page, int size, String sortBy, boolean ascending) {
        if (page < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y 100");
        }
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.ascending = ascending;
    }

    public PaginationParams(int page, int size) {
        this(page, size, null, true);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public boolean isAscending() {
        return ascending;
    }
}