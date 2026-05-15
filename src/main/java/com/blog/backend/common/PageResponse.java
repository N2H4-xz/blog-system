package com.blog.backend.common;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
        List<T> records,
        long total,
        int page,
        int pageSize,
        int totalPages
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages()
        );
    }
}
