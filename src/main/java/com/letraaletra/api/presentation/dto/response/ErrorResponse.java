package com.letraaletra.api.presentation.dto.response;

public record ErrorResponse(
        boolean success,
        String message
) {
}
