package com.letraaletra.api.shared.infrastructure.presentation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public final class Pageables {
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 50;

    private Pageables() {
    }

    public static Pageable sanitize(Pageable pageable, Set<String> allowedSorts, Sort defaultSort) {
        if (pageable == null) {
            return PageRequest.of(0, DEFAULT_SIZE, defaultSort != null ? defaultSort : Sort.unsorted());
        }

        int page = clampPage(pageable.getPageNumber());
        int size = clampSize(pageable.getPageSize());
        Sort sort = sanitizeSort(pageable.getSort(), allowedSorts);

        if (sort.isUnsorted() && defaultSort != null && defaultSort.isSorted()) {
            sort = defaultSort;
        }

        return PageRequest.of(page, size, sort);
    }

    public static Sort sanitizeSort(Sort sort, Set<String> allowedSorts) {
        if (sort == null || sort.isUnsorted() || allowedSorts == null || allowedSorts.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = sort.stream()
                .filter(order -> allowedSorts.contains(order.getProperty()))
                .toList();

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    public static int clampPage(int page) {
        return Math.max(0, page);
    }

    public static int clampSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
