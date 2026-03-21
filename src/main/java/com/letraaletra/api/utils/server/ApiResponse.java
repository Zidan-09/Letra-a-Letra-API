package com.letraaletra.api.utils.server;

import com.letraaletra.api.dto.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {
    public static <T> ResponseEntity<SuccessResponse<T>> success(
            T data,
            String message,
            HttpStatus status
    ) {
        return ResponseEntity
                .status(status)
                .body(new SuccessResponse<>(
                        true,
                        message,
                        data
                ));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success(T data) {
        return success(data, "ok", HttpStatus.OK);
    }
}
