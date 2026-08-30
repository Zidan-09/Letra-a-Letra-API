package com.letraaletra.api.features.ticket.application.input;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetMyTicketsInput(
        UUID userId,
        int page,
        int size,
        Sort sort
) {
}
