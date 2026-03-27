package com.letraaletra.api.presentation.dto.response;

public record SuccessResponse<T>(
        boolean success,
        String message,
        T data
) {
}
