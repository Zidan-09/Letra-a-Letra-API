package com.letraaletra.api.features.ticket.application.input;

import java.util.UUID;

public record GetMyTicketsInput(
        UUID userId,
        int page,
        int size,
        boolean ascending
) {
}
