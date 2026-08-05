package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ServerMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(
            DomainException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        MessageCode code = ex.getMessageCode();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        false,
                        code.getCode(),
                        code.getMessage())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        message
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("Invalid request");

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        message
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex
    ) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        false,
                        "INVALID_INPUT",
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        false,
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        logger.error("An internal error has been threw:", ex);

        request.setAttribute("AUDIT_EXCEPTION", ex);

        return ResponseEntity
                .status(500)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.INTERNAL_ERROR.getCode(),
                        ServerMessages.INTERNAL_ERROR.getMessage()
                ));
    }
}