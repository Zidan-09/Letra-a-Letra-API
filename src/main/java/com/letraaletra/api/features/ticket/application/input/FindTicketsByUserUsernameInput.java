package com.letraaletra.api.features.ticket.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

public record FindTicketsByUserUsernameInput(
        AuthenticatedUser principal,
        String username,
        int page,
        int size,
        Sort sort
) {
}