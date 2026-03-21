package com.letraaletra.api.exception;

import com.letraaletra.api.dto.response.ErrorResponse;
import com.letraaletra.api.exception.messages.ServerMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleAppException(AppException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(false, ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleGenericException() {
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse(false, ServerMessages.INTERNAL_ERROR.getMessage()));
    }
}
