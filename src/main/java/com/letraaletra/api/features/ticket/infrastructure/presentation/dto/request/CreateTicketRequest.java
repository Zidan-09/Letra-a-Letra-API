package com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.ticket.domain.TicketCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotNull
        TicketCategory category,

        @NotBlank
        @Size(min = 5, max = 100)
        String subject,

        @NotBlank
        @Size(min = 10, max = 4000)
        String description
) {
}
