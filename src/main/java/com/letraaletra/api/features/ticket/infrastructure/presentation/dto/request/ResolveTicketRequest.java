package com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.Size;

public record ResolveTicketRequest(
        @Size(max = 1000)
        String resolutionNote
) {
}
