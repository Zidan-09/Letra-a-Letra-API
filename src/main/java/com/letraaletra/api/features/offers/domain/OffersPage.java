package com.letraaletra.api.features.offers.domain;

import org.springframework.data.domain.Sort;

public record OffersPage(
        int page,
        int size,
        Sort sort
) {
}
