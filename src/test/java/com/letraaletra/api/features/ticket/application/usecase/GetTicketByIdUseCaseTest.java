package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.ticket.application.input.GetTicketByIdInput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.exception.TicketNotFoundException;
import com.letraaletra.api.features.ticket.domain.repository.FindTicket;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.exceptions.UserIsNotAdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTicketByIdUseCaseTest {

    @Mock
    private FindTicket findTicket;

    @Mock
    private AdminChecker adminChecker;

    private GetTicketByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetTicketByIdUseCase(findTicket, adminChecker);
    }

    private Ticket ticketOwnedBy(UUID ownerId) {
        return Ticket.create(
                ownerId,
                TicketCategory.BUG,
                "Cannot login",
                "The game crashes when I try to login"
        );
    }

    @Test
    @DisplayName("Owner should fetch own ticket without admin permission check")
    void ownerShouldFetchOwnTicket() {
        UUID ownerId = UUID.randomUUID();
        Ticket ticket = ticketOwnedBy(ownerId);
        when(findTicket.findById(ticket.getTicketId())).thenReturn(Optional.of(ticket));

        var output = useCase.execute(new GetTicketByIdInput(ownerPrincipal(ownerId), ticket.getTicketId()));

        assertEquals(ticket.getTicketId(), output.ticket().getTicketId());
        verifyNoInteractions(adminChecker);
    }

    @Test
    @DisplayName("Common user fetching a foreign ticket should be rejected by the AdminChecker")
    void commonUserShouldNotFetchForeignTicket() {
        UUID ownerId = UUID.randomUUID();
        UUID intruderId = UUID.randomUUID();
        Ticket ticket = ticketOwnedBy(ownerId);
        when(findTicket.findById(ticket.getTicketId())).thenReturn(Optional.of(ticket));
        doThrow(UserIsNotAdminException.class)
                .when(adminChecker)
                .check(any(), eq(PermissionKey.TICKET), eq(PermissionAction.VIEW));

        assertThrows(UserIsNotAdminException.class, () -> useCase.execute(
                new GetTicketByIdInput(ownerPrincipal(intruderId), ticket.getTicketId())
        ));

        verify(adminChecker).check(any(), eq(PermissionKey.TICKET), eq(PermissionAction.VIEW));
    }

    @Test
    @DisplayName("Authorized admin should fetch a foreign ticket")
    void authorizedAdminShouldFetchForeignTicket() {
        UUID ownerId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Ticket ticket = ticketOwnedBy(ownerId);
        when(findTicket.findById(ticket.getTicketId())).thenReturn(Optional.of(ticket));
        doNothing().when(adminChecker).check(any(), eq(PermissionKey.TICKET), eq(PermissionAction.VIEW));

        AuthenticatedUser adminPrincipal = new AuthenticatedUser(adminId, "Admin", true, false);
        var output = useCase.execute(new GetTicketByIdInput(adminPrincipal, ticket.getTicketId()));

        assertEquals(ticket.getTicketId(), output.ticket().getTicketId());
    }

    @Test
    @DisplayName("Missing ticket should raise TicketNotFoundException")
    void missingTicketShouldRaiseNotFound() {
        when(findTicket.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> useCase.execute(
                new GetTicketByIdInput(ownerPrincipal(UUID.randomUUID()), UUID.randomUUID())
        ));
    }

    private AuthenticatedUser ownerPrincipal(UUID id) {
        return new AuthenticatedUser(id, "player", false, false);
    }
}
