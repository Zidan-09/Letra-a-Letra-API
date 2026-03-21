package com.letraaletra.api.dto.response;

public record ErrorResponse(
        boolean success,
        String message
) {
}
