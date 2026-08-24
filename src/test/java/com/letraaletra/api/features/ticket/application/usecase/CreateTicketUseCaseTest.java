package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.ticket.application.input.CreateTicketInput;
import com.letraaletra.api.features.ticket.application.output.CreateTicketOutput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateTicketUseCaseTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private BusinessAuditRecorder auditRecorder;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    @Captor
    private ArgumentCaptor<AuditEvent> auditCaptor;

    private CreateTicketUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateTicketUseCase(ticketRepository, auditRecorder);
    }

    private AuthenticatedUser userPrincipal() {
        return new AuthenticatedUser(UUID.randomUUID(), "player", false, false);
    }

    @Test
    @DisplayName("Should create a PENDING ticket owned by the authenticated user")
    void shouldCreatePendingTicketForAuthenticatedUser() {
        AuthenticatedUser principal = userPrincipal();
        CreateTicketInput input = new CreateTicketInput(
                principal,
                TicketCategory.BUG,
                "Cannot login",
                "The game crashes when I try to login"
        );

        CreateTicketOutput output = useCase.execute(input);

        verify(ticketRepository, times(1)).save(ticketCaptor.capture());
        Ticket saved = ticketCaptor.getValue();

        assertEquals(principal.auth(), saved.getUserId());
        assertEquals(TicketStatus.PENDING, saved.getStatus());
        assertEquals(TicketCategory.BUG, saved.getCategory());
        assertEquals(output.ticket().getTicketId(), saved.getTicketId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Should record TICKET_CREATED audit event targeting the owner")
    void shouldRecordTicketCreatedAuditEvent() {
        AuthenticatedUser principal = userPrincipal();
        CreateTicketInput input = new CreateTicketInput(
                principal,
                TicketCategory.FEEDBACK,
                "Great update",
                "Loved the new ranking season rewards"
        );

        useCase.execute(input);

        verify(auditRecorder, times(1)).record(auditCaptor.capture());
        AuditEvent event = auditCaptor.getValue();

        assertEquals(AuditEventType.TICKET_CREATED, event.eventType());
        assertEquals(AuditResourceType.TICKET, event.resourceType());
        assertEquals(principal.auth(), event.targetUserId());
        assertEquals(principal.auth(), event.actor().id());
        assertNotNull(event.resourceId());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
}
