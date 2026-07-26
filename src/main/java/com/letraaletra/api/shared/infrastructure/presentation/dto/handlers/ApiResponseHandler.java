package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseHandler {
    public static <T> ResponseEntity<SuccessResponse<T>> success(
            T data,
            HttpStatus status
    ) {
        return ResponseEntity
                .status(status)
                .body(new SuccessResponse<>(
                        true,
                        data
                ));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success(T data) {
        return success(data, HttpStatus.OK);
    }
}
