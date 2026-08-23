package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.MessageCode;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ServerMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Set<String> AUDITED_HTTP_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final BusinessAuditRecorder auditRecorder;

    public GlobalExceptionHandler(BusinessAuditRecorder auditRecorder) {
        this.auditRecorder = auditRecorder;
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(
            DomainException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        recordHttpFailure(ex, request, HttpStatus.BAD_REQUEST.value());

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
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

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
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

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

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        false,
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage()
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

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex,
            HttpServletRequest request
    ) {
        request.setAttribute("AUDIT_EXCEPTION", ex);

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(ex.getMessage());

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        false,
                        "INVALID_REQUEST",
                        message
                )
        );
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

    private void recordHttpFailure(Exception ex, HttpServletRequest request, int status) {
        try {
            if (!AUDITED_HTTP_METHODS.contains(request.getMethod())) {
                return;
            }

            Throwable rootCause = ex;

            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }

            auditRecorder.recordFailure(AuditEvent.builder()
                    .category(AuditCategory.OPERATION)
                    .eventType(AuditEventType.COMMAND_FAILED)
                    .actor(requestActor())
                    .resourceType(AuditResourceType.USER)
                    .resourceId(resolveResourceId())
                    .failureReason(rootCause.getMessage())
                    .sourceType(AuditSourceType.HTTP)
                    .metadata(Map.of(
                            "status", status,
                            "exceptionType", rootCause.getClass().getSimpleName()
                    ))
                    .build());
        } catch (Exception ignored) {
            // FAILURE recording is best-effort; never changes the original response.
        }
    }

    private AuditActor requestActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            AuditActorType type = user.isAdmin() ? AuditActorType.ADMIN : AuditActorType.USER;

            return new AuditActor(type, user.auth(), user.name());
        }

        return new AuditActor(AuditActorType.SYSTEM, null, "ANONYMOUS");
    }

    private String resolveResourceId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return user.auth().toString();
        }

        return "anonymous";
    }
}