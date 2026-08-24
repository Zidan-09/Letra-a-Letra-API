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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
}
