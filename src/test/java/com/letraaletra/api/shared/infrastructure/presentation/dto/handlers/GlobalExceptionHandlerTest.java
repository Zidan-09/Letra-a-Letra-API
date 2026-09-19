package com.letraaletra.api.shared.infrastructure.presentation.dto.handlers;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - caracterização do mapeamento de erros HTTP")
class GlobalExceptionHandlerTest {

    private enum TestMessages implements MessageCode {
        ROOM_IS_FULL;

        @Override
        public String getCode() {
            return name();
        }

        @Override
        public String getMessage() {
            return "room_is_full";
        }
    }

    @Mock
    private HttpCommandFailureAuditor failureAuditor;

    private GlobalExceptionHandler handler;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(failureAuditor);
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("DomainException responde 400 com code/message do MessageCode e grava falha em POST")
    void shouldMapDomainExceptionAndRecordFailureOnPost() {
        request.setMethod("POST");
        DomainException exception = new DomainException(TestMessages.ROOM_IS_FULL);

        ResponseEntity<ErrorResponse> response = handler.handleHttpException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().success());
        assertEquals("ROOM_IS_FULL", response.getBody().code());
        assertEquals("room_is_full", response.getBody().message());

        verify(failureAuditor).recordFailure(request, exception, 400);
    }

    @Test
    @DisplayName("método GET delega ao auditor, que decide não gravar")
    void shouldDelegateAuditDecisionForGet() {
        request.setMethod("GET");

        DomainException exception = new DomainException(TestMessages.ROOM_IS_FULL);
        handler.handleHttpException(exception, request);

        verify(failureAuditor).recordFailure(request, exception, 400);
    }

    @Test
    @DisplayName("ConstraintViolationException responde 400 INVALID_REQUEST")
    void shouldMapConstraintViolation() {
        request.setMethod("POST");

        Set<jakarta.validation.ConstraintViolation<?>> violations = Set.of();
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(
                new ConstraintViolationException("invalid", violations), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_REQUEST", response.getBody().code());
        verify(failureAuditor, never()).recordFailure(any(), any(), anyInt());
    }

    @Test
    @DisplayName("recurso inexistente responde 404 RESOURCE_NOT_FOUND")
    void shouldMapNoResourceFound() {
        request.setMethod("POST");

        ResponseEntity<ErrorResponse> response = handler.handleNoResourceFound(
                new NoResourceFoundException(
                        org.springframework.http.HttpMethod.GET, "/missing", "not found"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().code());
    }

    @Test
    @DisplayName("método não suportado responde 405 METHOD_NOT_ALLOWED")
    void shouldMapMethodNotSupported() {
        request.setMethod("POST");

        ResponseEntity<ErrorResponse> response = handler.handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("TRACE"), request);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("METHOD_NOT_ALLOWED", response.getBody().code());
    }

    @Test
    @DisplayName("violação de integridade responde 409 CONFLICT")
    void shouldMapDataIntegrityViolation() {
        request.setMethod("POST");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("dup key"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICT", response.getBody().code());
        assertEquals("the request conflicts with the current state of the resource",
                response.getBody().message());
    }

    @Test
    @DisplayName("resultado não-único responde 409 CONFLICT em vez de 500")
    void shouldMapIncorrectResultSize() {
        request.setMethod("PATCH");

        ResponseEntity<ErrorResponse> response = handler.handleIncorrectResultSize(
                new IncorrectResultSizeDataAccessException("Query did not return a unique result: 2", 2), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICT", response.getBody().code());
    }

    @Test
    @DisplayName("NonUniqueResultException responde 409 CONFLICT em vez de 500")
    void shouldMapNonUniqueResult() {
        request.setMethod("PATCH");

        ResponseEntity<ErrorResponse> response = handler.handleNonUniqueResult(
                new jakarta.persistence.NonUniqueResultException("2 results"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICT", response.getBody().code());
    }

    @Test
    @DisplayName("exceção genérica responde 500 INTERNAL_ERROR e grava falha")
    void shouldMapGenericException() {
        request.setMethod("DELETE");
        IllegalStateException exception =
                new IllegalStateException("boom", new RuntimeException("root cause msg"));

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody().code());

        verify(failureAuditor).recordFailure(request, exception, 500);
    }

    @Test
    @DisplayName("MethodArgumentNotValid com typeMismatch não expõe assinatura nem detalhes internos")
    void shouldSanitizeMethodArgumentNotValid() {
        request.setMethod("PUT");

        org.springframework.validation.BeanPropertyBindingResult bindingResult =
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "updateItemRequest");
        bindingResult.addError(new org.springframework.validation.FieldError(
                "updateItemRequest",
                "isNewAsset",
                null,
                false,
                new String[]{"typeMismatch.updateItemRequest.isNewAsset"},
                null,
                "Failed to convert value of type 'null' to required type 'boolean'; Failed to convert from type [null] to type [boolean] for value [null]"));

        org.springframework.core.MethodParameter parameter = null;
        try {
            parameter = new org.springframework.core.MethodParameter(
                    com.letraaletra.api.features.items.infrastructure.controller.UpdateItemController.class
                            .getMethod("handle",
                                    com.letraaletra.api.shared.domain.AuthenticatedUser.class,
                                    java.util.UUID.class,
                                    com.letraaletra.api.features.items.infrastructure.presentation.dto.request.UpdateItemRequest.class),
                    2);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }

        org.springframework.web.bind.MethodArgumentNotValidException exception =
                new org.springframework.web.bind.MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_REQUEST", response.getBody().code());
        assertEquals("Requisição inválida.", response.getBody().message());
        assertFalse(response.getBody().message().contains("Failed to convert"));
        assertFalse(response.getBody().message().contains("UpdateItemController"));
        assertFalse(response.getBody().message().contains("ResponseEntity"));
    }

    @Test
    @DisplayName("BindException não expõe mensagem interna")
    void shouldSanitizeBindException() {
        request.setMethod("PUT");

        org.springframework.validation.BeanPropertyBindingResult bindingResult =
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "updateItemRequest");
        bindingResult.addError(new org.springframework.validation.FieldError(
                "updateItemRequest", "isNewAsset", null, false, null, null,
                "Failed to convert value of type 'null' to required type 'boolean'"));

        org.springframework.validation.BindException exception =
                new org.springframework.validation.BindException(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleBindException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_REQUEST", response.getBody().code());
        assertEquals("Requisição inválida.", response.getBody().message());
    }

    @Test
    @DisplayName("HandlerMethodValidationException responde 400 genérico sem vazar detalhes")
    void shouldSanitizeHandlerMethodValidation() {
        request.setMethod("PUT");

        org.springframework.validation.method.MethodValidationResult validationResult =
                org.mockito.Mockito.mock(org.springframework.validation.method.MethodValidationResult.class);
        org.springframework.web.method.annotation.HandlerMethodValidationException exception =
                new org.springframework.web.method.annotation.HandlerMethodValidationException(validationResult);

        ResponseEntity<ErrorResponse> response = handler.handleHandlerMethodValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_REQUEST", response.getBody().code());
        assertEquals("Requisição inválida.", response.getBody().message());
    }

    @Test
    @DisplayName("IllegalArgumentException de framework é sanitizada; do nosso código preserva mensagem")
    void shouldSanitizeFrameworkIllegalArgumentButKeepOwnCode() {
        request.setMethod("POST");

        IllegalArgumentException frameworkEx = new IllegalArgumentException(
                "Validation failed for argument [2] in public org.springframework.http.ResponseEntity<?> handle(...): Failed to convert");
        frameworkEx.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor",
                        "bindRequestParameters", "ServletModelAttributeMethodProcessor.java", 100)
        });

        ResponseEntity<ErrorResponse> sanitized = handler.handleIllegalArgument(frameworkEx, request);

        assertEquals(HttpStatus.BAD_REQUEST, sanitized.getStatusCode());
        assertEquals("Requisição inválida.", sanitized.getBody().message());
        assertFalse(sanitized.getBody().message().contains("Validation failed"));
        assertFalse(sanitized.getBody().message().contains("org.springframework"));

        IllegalArgumentException ownEx = new IllegalArgumentException("Invalid path.");
        ownEx.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("com.letraaletra.api.features.admin.infrastructure.controller.FindGameLogsController",
                        "handle", "FindGameLogsController.java", 206)
        });

        ResponseEntity<ErrorResponse> preserved = handler.handleIllegalArgument(ownEx, request);

        assertEquals(HttpStatus.BAD_REQUEST, preserved.getStatusCode());
        assertEquals("Invalid path.", preserved.getBody().message());
    }

    @Test
    @DisplayName("NoResourceFound não expõe mensagem interna")
    void shouldSanitizeNoResourceFound() {
        request.setMethod("GET");

        ResponseEntity<ErrorResponse> response = handler.handleNoResourceFound(
                new NoResourceFoundException(
                        org.springframework.http.HttpMethod.GET, "/missing", "No static resource missing."),
                request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().code());
        assertEquals("Recurso não encontrado.", response.getBody().message());
        assertFalse(response.getBody().message().contains("No static resource"));
    }

    @Test
    @DisplayName("parâmetro ausente e type mismatch retornam 400 genérico")
    void shouldSanitizeFrameworkBadRequests() {
        request.setMethod("GET");

        ResponseEntity<ErrorResponse> missingParam = handler.handleFrameworkBadRequest(
                new org.springframework.web.bind.MissingServletRequestParameterException("page", "int"),
                request);
        assertEquals(HttpStatus.BAD_REQUEST, missingParam.getStatusCode());
        assertEquals("Requisição inválida.", missingParam.getBody().message());

        ResponseEntity<ErrorResponse> multipart = handler.handleFrameworkBadRequest(
                new org.springframework.web.multipart.MultipartException("Failed to parse multipart servlet request"),
                request);
        assertEquals(HttpStatus.BAD_REQUEST, multipart.getStatusCode());
        assertEquals("Requisição inválida.", multipart.getBody().message());
    }

    @Test
    @DisplayName("ResponseStatusException não expõe reason interno")
    void shouldSanitizeResponseStatusException() {
        request.setMethod("GET");

        org.springframework.web.server.ResponseStatusException notFound =
                new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "secret internal reason with com.letraaletra.api.Foo.handle");

        ResponseEntity<ErrorResponse> response = handler.handleResponseStatus(notFound, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso não encontrado.", response.getBody().message());
        assertFalse(response.getBody().message().contains("secret"));
    }

    @Test
    @DisplayName("ErrorResponseException 400 é sanitizada sem expor detail")
    void shouldSanitizeErrorResponseException() {
        request.setMethod("GET");

        org.springframework.web.ErrorResponseException exception =
                new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Validation failed for argument [2] in public ResponseEntity handle(...)");

        ResponseEntity<ErrorResponse> response = handler.handleSpringErrorResponse(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Requisição inválida.", response.getBody().message());
        assertFalse(response.getBody().message().contains("Validation failed"));
    }
}
