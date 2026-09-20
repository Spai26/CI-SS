package com.cuidar.api.common.web;

import java.util.List;

/**
 * Generic paginated response wrapper for REST endpoints.
 *
 * <p>Pure Java record (no Spring Data / JPA dependency) so it can be built
 * from jOOQ results, manual slicing, or anything else.</p>
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }
}