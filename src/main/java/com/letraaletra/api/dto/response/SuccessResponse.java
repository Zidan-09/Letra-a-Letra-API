package com.letraaletra.api.dto.response;

public record SuccessResponse<T>(
        boolean success,
        String message,
        T data
) {
}
