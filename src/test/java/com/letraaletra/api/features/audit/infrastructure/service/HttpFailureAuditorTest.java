package com.letraaletra.api.features.audit.infrastructure.service;

import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("HttpFailureAuditor - política de gravação de falhas HTTP")
class HttpFailureAuditorTest {

    @Mock
    private BusinessAuditRecorder auditRecorder;

    private HttpFailureAuditor auditor;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        auditor = new HttpFailureAuditor(auditRecorder);
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        SecurityContextHolder.clearContext();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST com DomainException grava COMMAND_FAILED com causa raiz e ator anônimo")
    void shouldRecordFailureForPost() {
        request.setMethod("POST");
        IllegalStateException exception =
                new IllegalStateException("wrap", new RuntimeException("root cause msg"));

        auditor.recordFailure(request, exception, 400);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditRecorder).recordFailure(captor.capture());
        AuditEvent event = captor.getValue();

        assertEquals("root cause msg", event.failureReason());
        assertEquals("anonymous", event.resourceId());
        assertEquals(com.letraaletra.api.features.audit.domain.AuditCategory.OPERATION, event.category());
        assertEquals(com.letraaletra.api.features.audit.domain.AuditEventType.COMMAND_FAILED, event.eventType());
        assertEquals(com.letraaletra.api.features.audit.domain.AuditSourceType.HTTP, event.sourceType());
        assertInstanceOf(com.letraaletra.api.features.audit.domain.AuditActor.class, event.actor());
    }

    @Test
    @DisplayName("GET não grava auditoria de falha")
    void shouldSkipGet() {
        request.setMethod("GET");

        auditor.recordFailure(request, new IllegalStateException("x"), 500);

        verifyNoInteractions(auditRecorder);
    }

    @Test
    @DisplayName("falha do próprio auditor nunca se propaga")
    void shouldNeverPropagateAuditorFailure() {
        request.setMethod("POST");
        org.mockito.Mockito.doThrow(new RuntimeException("audit down"))
                .when(auditRecorder).recordFailure(org.mockito.ArgumentMatchers.any());

        auditor.recordFailure(request, new IllegalStateException("x"), 400);

        verify(auditRecorder).recordFailure(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("usuário autenticado é usado como ator USER e resourceId")
    void shouldUseAuthenticatedUserAsActor() {
        request.setMethod("PUT");

        var principal = new com.letraaletra.api.shared.domain.AuthenticatedUser(
                java.util.UUID.randomUUID(), "alice", false, false);
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        org.mockito.Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        org.mockito.Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);

        try {
            auditor.recordFailure(request, new RuntimeException("direct"), 400);
        } finally {
            SecurityContextHolder.clearContext();
        }

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditRecorder).recordFailure(captor.capture());
        assertEquals(principal.auth().toString(), captor.getValue().resourceId());
        assertEquals(com.letraaletra.api.features.audit.domain.AuditActorType.USER,
                captor.getValue().actor().type());
        assertEquals("alice", captor.getValue().actor().name());
    }
}
