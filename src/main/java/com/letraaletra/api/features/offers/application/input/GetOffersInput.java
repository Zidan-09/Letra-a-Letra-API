package com.letraaletra.api.features.offers.application.input;

import org.springframework.data.domain.Sort;

public record GetOffersInput(
        int page,
        int size,
        Sort sort
) {
}
