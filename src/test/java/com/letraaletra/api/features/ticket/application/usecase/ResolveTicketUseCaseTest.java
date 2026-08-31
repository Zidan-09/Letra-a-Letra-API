package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.ticket.application.input.ResolveTicketInput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketDetails;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.domain.exception.InvalidTicketStatusException;
import com.letraaletra.api.features.ticket.domain.exception.TicketNotFoundException;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveTicketUseCaseTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AdminChecker adminChecker;

    @Mock
    private BusinessAuditRecorder auditRecorder;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    @Captor
    private ArgumentCaptor<AuditEvent> auditCaptor;

    private ResolveTicketUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ResolveTicketUseCase(ticketRepository, adminChecker, auditRecorder);
    }

    private Ticket pendingTicket(UUID ownerId) {
        return Ticket.create(
                ownerId,
                TicketCategory.BUG,
                "Cannot login",
                "The game crashes when I try to login"
        );
    }

    private TicketDetails buildTicketDetails(Ticket ticket) {
        return new TicketDetails(
                ticket.getTicketId(),
                ticket.getUserId(),
                "player",
                ticket.getCategory(),
                TicketStatus.RESOLVED,
                ticket.getSubject(),
                ticket.getDescription(),
                "Fixed in patch 1.2",
                UUID.randomUUID(),
                "Admin",
                LocalDateTime.now(),
                ticket.getCreatedAt()
        );
    }

    @Test
    @DisplayName("Admin with TICKET/EDIT should resolve a pending ticket and register authorship, date and note")
    void adminShouldResolvePendingTicket() {
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        Ticket ticket = pendingTicket(UUID.randomUUID());
        TicketDetails ticketDetails = buildTicketDetails(ticket);
        when(ticketRepository.findById(ticket.getTicketId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.findDetailsById(ticket.getTicketId())).thenReturn(Optional.of(ticketDetails));

        var output = useCase.execute(new ResolveTicketInput(admin, ticket.getTicketId(), " Fixed in patch 1.2 "));

        verify(ticketRepository, times(1)).save(ticketCaptor.capture());
        Ticket saved = ticketCaptor.getValue();

        assertEquals(TicketStatus.RESOLVED, saved.getStatus());
        assertEquals(admin.auth(), saved.getResolvedByAdminId());
        assertNotNull(saved.getResolvedAt());
        assertEquals("Fixed in patch 1.2", saved.getResolutionNote());
        assertEquals(output.ticket().ticketId(), saved.getTicketId());
    }

    @Test
    @DisplayName("Resolution should record a TICKET_RESOLVED audit event with before and after states")
    void resolutionShouldRecordAuditEvent() {
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        UUID ownerId = UUID.randomUUID();
        Ticket ticket = pendingTicket(ownerId);
        TicketDetails ticketDetails = buildTicketDetails(ticket);
        when(ticketRepository.findById(ticket.getTicketId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.findDetailsById(ticket.getTicketId())).thenReturn(Optional.of(ticketDetails));

        useCase.execute(new ResolveTicketInput(admin, ticket.getTicketId(), "Fixed"));

        verify(auditRecorder, times(1)).record(auditCaptor.capture());
        AuditEvent event = auditCaptor.getValue();

        assertEquals(AuditEventType.TICKET_RESOLVED, event.eventType());
        assertEquals(AuditResourceType.TICKET, event.resourceType());
        assertEquals(ownerId, event.targetUserId());
        assertEquals(admin.auth(), event.actor().id());

        Map<String, Object> beforeState = event.beforeState();
        Map<String, Object> afterState = event.afterState();
        assertEquals(TicketStatus.PENDING.name(), beforeState.get("status"));
        assertEquals(TicketStatus.RESOLVED.name(), afterState.get("status"));
        assertEquals("Fixed", event.metadata().get("resolutionNote"));
    }

    @Test
    @DisplayName("Common user must never resolve: authorization fails before repository usage")
    void commonUserMustNeverResolve() {
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "player", false, false);
        org.mockito.Mockito.doThrow(UserIsNotAdminException.class)
                .when(adminChecker)
                .check(user, PermissionKey.TICKET, PermissionAction.EDIT);

        assertThrows(UserIsNotAdminException.class, () -> useCase.execute(
                new ResolveTicketInput(user, UUID.randomUUID(), null)
        ));

        verify(ticketRepository, never()).findById(any(UUID.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(auditRecorder);
    }

    @Test
    @DisplayName("Admin without TICKET/EDIT permission must not resolve")
    void adminWithoutEditPermissionMustNotResolve() {
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        org.mockito.Mockito.doThrow(PermissionDeniedException.class)
                .when(adminChecker)
                .check(admin, PermissionKey.TICKET, PermissionAction.EDIT);

        assertThrows(PermissionDeniedException.class, () -> useCase.execute(
                new ResolveTicketInput(admin, UUID.randomUUID(), null)
        ));

        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(auditRecorder);
    }

    @Test
    @DisplayName("Resolving a missing ticket should raise TicketNotFoundException")
    void missingTicketShouldRaiseNotFound() {
        when(ticketRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);

        assertThrows(TicketNotFoundException.class, () -> useCase.execute(
                new ResolveTicketInput(admin, UUID.randomUUID(), null)
        ));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Already resolved ticket cannot be resolved again")
    void alreadyResolvedTicketCannotBeResolvedAgain() {
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        Ticket ticket = pendingTicket(UUID.randomUUID());
        ticket.resolve(admin.auth(), "first resolution");
        when(ticketRepository.findById(ticket.getTicketId())).thenReturn(Optional.of(ticket));

        assertThrows(InvalidTicketStatusException.class, () -> useCase.execute(
                new ResolveTicketInput(admin, ticket.getTicketId(), "second attempt")
        ));

        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(auditRecorder);
    }
}
