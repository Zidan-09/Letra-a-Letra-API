package com.letraaletra.api.shared.infrastructure.presentation.dto.response;

public record SuccessResponse<T>(
        boolean success,
        T data
) {
}
