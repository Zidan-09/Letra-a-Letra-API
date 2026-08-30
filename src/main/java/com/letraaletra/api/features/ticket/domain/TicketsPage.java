package com.letraaletra.api.features.ticket.domain;

import org.springframework.data.domain.Sort;

public record TicketsPage(
        int page,
        int size,
        Sort sort
) {
}