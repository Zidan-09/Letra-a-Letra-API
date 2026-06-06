package com.letraaletra.api.shared.infrastructure.presentation.dto.response;

public record ErrorResponse(
        boolean success,
        String message
) {
}
