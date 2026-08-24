package com.letraaletra.api.features.ticket.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record GetTicketByIdInput(
        AuthenticatedUser principal,
        UUID ticketId
) {
}
