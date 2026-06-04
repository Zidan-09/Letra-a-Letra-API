package com.letraaletra.api.presentation.dto.response.http;

public record SuccessResponse<T>(
        boolean success,
        String message,
        T data
) {
}
