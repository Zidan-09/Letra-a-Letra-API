package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ServerMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final HttpCommandFailureAuditor failureAuditor;
    private final DomainExceptionHttpMapper domainExceptionHttpMapper;

    public GlobalExceptionHandler(
            HttpCommandFailureAuditor failureAuditor,
            DomainExceptionHttpMapper domainExceptionHttpMapper
    ) {
        this.failureAuditor = failureAuditor;
        this.domainExceptionHttpMapper = domainExceptionHttpMapper;
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(            DomainException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        HttpStatus status = domainExceptionHttpMapper.resolve(ex);

        recordHttpFailure(ex, request, status.value());

        MessageCode code = ex.getMessageCode();

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        false,
                        code.getCode(),
                        code.getMessage())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Validation failed for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        "Requisição inválida."
                ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Method validation failed for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        "Requisição inválida."
                ));
    }

    @ExceptionHandler(org.springframework.validation.method.MethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(
            org.springframework.validation.method.MethodValidationException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Method validation failed for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        "Requisição inválida."
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        if (isThrownByOwnCode(ex)) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                            false,
                            "INVALID_REQUEST",
                            ex.getMessage() != null ? ex.getMessage() : "Invalid request"
                    ));
        }

        logger.warn("Illegal argument for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        "Requisição inválida."
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Constraint violation for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        "Requisição inválida."
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        false,
                        "INVALID_INPUT",
                        "O corpo da requisição é inválido."
                )
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Resource not found for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        false,
                        "RESOURCE_NOT_FOUND",
                        "Recurso não encontrado."
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse(
                        false,
                        "METHOD_NOT_ALLOWED",
                        "O método HTTP não é suportado para esta rota."
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.CONFLICT.getCode(),
                        ServerMessages.CONFLICT.getMessage()
                ));
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectResultSize(
            IncorrectResultSizeDataAccessException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Query returned a non-unique result: {}", ex.getMessage());

        recordHttpFailure(ex, request, HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.CONFLICT.getCode(),
                        ServerMessages.CONFLICT.getMessage()
                ));
    }

    @ExceptionHandler(jakarta.persistence.NonUniqueResultException.class)
    public ResponseEntity<ErrorResponse> handleNonUniqueResult(
            jakarta.persistence.NonUniqueResultException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Non-unique query result: {}", ex.getMessage());

        recordHttpFailure(ex, request, HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.CONFLICT.getCode(),
                        ServerMessages.CONFLICT.getMessage()
                ));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Binding failed for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        "Requisição inválida."
                )
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class,
            MissingServletRequestPartException.class,
            ServletRequestBindingException.class,
            MethodArgumentTypeMismatchException.class,
            TypeMismatchException.class,
            ConversionNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MultipartException.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<ErrorResponse> handleFrameworkBadRequest(
            Exception ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Bad request for {}: {}: {}",
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());

        if (ex instanceof MaxUploadSizeExceededException) {
            return ResponseEntity
                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(new ErrorResponse(
                            false,
                            "INVALID_REQUEST",
                            "Requisição inválida."
                    ));
        }

        if (ex instanceof HttpMediaTypeNotSupportedException) {
            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(new ErrorResponse(
                            false,
                            "INVALID_REQUEST",
                            "Requisição inválida."
                    ));
        }

        if (ex instanceof HttpMediaTypeNotAcceptableException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ErrorResponse(
                            false,
                            "INVALID_REQUEST",
                            "Requisição inválida."
                    ));
        }

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        "Requisição inválida."
                ));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Access denied for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.FORBIDDEN.getCode(),
                        ServerMessages.FORBIDDEN.getMessage()
                ));
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            org.springframework.security.core.AuthenticationException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        logger.warn("Authentication failed for request {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.UNAUTHORIZED.getCode(),
                        ServerMessages.UNAUTHORIZED.getMessage()
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        int status = ex.getStatusCode().value();

        logger.warn("Response status {} for request {}: {}",
                status, request.getRequestURI(), ex.getMessage());

        if (status == HttpStatus.NOT_FOUND.value()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                    false,
                    "RESOURCE_NOT_FOUND",
                    "Recurso não encontrado."
            ));
        }

        if (status == HttpStatus.FORBIDDEN.value()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    false,
                    ServerMessages.FORBIDDEN.getCode(),
                    ServerMessages.FORBIDDEN.getMessage()
            ));
        }

        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                    false,
                    ServerMessages.UNAUTHORIZED.getCode(),
                    ServerMessages.UNAUTHORIZED.getMessage()
            ));
        }

        if (status >= 400 && status < 500) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(
                    false,
                    "INVALID_REQUEST",
                    "Requisição inválida."
            ));
        }

        logger.error("An internal error has been threw:", ex);

        recordHttpFailure(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.INTERNAL_ERROR.getCode(),
                        ServerMessages.INTERNAL_ERROR.getMessage()
                ));
    }

    @ExceptionHandler(org.springframework.web.ErrorResponseException.class)
    public ResponseEntity<ErrorResponse> handleSpringErrorResponse(
            org.springframework.web.ErrorResponseException ex,
            HttpServletRequest request
    ) {
        if (request != null) {
            request.setAttribute("AUDIT_EXCEPTION", ex);
        }

        int status = ex.getStatusCode().value();

        logger.warn("Framework error {} for request {}: {}",
                status,
                request != null ? request.getRequestURI() : "unknown",
                ex.getMessage());

        if (status == HttpStatus.NOT_FOUND.value()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                    false,
                    "RESOURCE_NOT_FOUND",
                    "Recurso não encontrado."
            ));
        }

        if (status == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse(
                    false,
                    "METHOD_NOT_ALLOWED",
                    "O método HTTP não é suportado para esta rota."
            ));
        }

        if (status == HttpStatus.FORBIDDEN.value()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    false,
                    ServerMessages.FORBIDDEN.getCode(),
                    ServerMessages.FORBIDDEN.getMessage()
            ));
        }

        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                    false,
                    ServerMessages.UNAUTHORIZED.getCode(),
                    ServerMessages.UNAUTHORIZED.getMessage()
            ));
        }

        if (status == HttpStatus.CONFLICT.value()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                    false,
                    ServerMessages.CONFLICT.getCode(),
                    ServerMessages.CONFLICT.getMessage()
            ));
        }

        if (status >= 400 && status < 500) {
            return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(
                    false,
                    "INVALID_REQUEST",
                    "Requisição inválida."
            ));
        }

        logger.error("An internal error has been threw:", ex);

        if (request != null) {
            recordHttpFailure(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.INTERNAL_ERROR.getCode(),
                        ServerMessages.INTERNAL_ERROR.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        logger.error("An internal error has been threw:", ex);

        request.setAttribute("AUDIT_EXCEPTION", ex);

        recordHttpFailure(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        false,
                        ServerMessages.INTERNAL_ERROR.getCode(),
                        ServerMessages.INTERNAL_ERROR.getMessage()
                ));
    }

    private boolean isThrownByOwnCode(Throwable ex) {
        StackTraceElement[] trace = ex.getStackTrace();
        if (trace == null || trace.length == 0) {
            return false;
        }
        String origin = trace[0].getClassName();
        return origin != null && origin.startsWith("com.letraaletra.");
    }

    private void recordHttpFailure(Exception ex, HttpServletRequest request, int status) {
        failureAuditor.recordFailure(request, ex, status);
    }
}
