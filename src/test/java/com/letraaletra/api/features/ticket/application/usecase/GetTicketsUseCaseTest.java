package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTicketsUseCaseTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AdminChecker adminChecker;

    @Captor
    private ArgumentCaptor<TicketFilter> filterCaptor;

    private GetTicketsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetTicketsUseCase(ticketRepository, adminChecker);
    }

    @Test
    @DisplayName("Should require TICKET/VIEW permission and forward filters with normalized pagination")
    void shouldRequirePermissionAndForwardFilters() {
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
        UUID targetUserId = UUID.randomUUID();
        when(ticketRepository.findTickets(any(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(Page.empty());

        useCase.execute(new GetTicketsInput(
                admin,
                TicketStatus.PENDING,
                null,
                targetUserId,
                -1,
                500,
                true
        ));

        verify(adminChecker).check(admin, PermissionKey.TICKET, PermissionAction.VIEW);
        verify(ticketRepository).findTickets(filterCaptor.capture(), eq(0), eq(200), eq(true));

        TicketFilter filter = filterCaptor.getValue();
        assertEquals(TicketStatus.PENDING, filter.status());
        assertEquals(targetUserId, filter.userId());
    }

    @Test
    @DisplayName("Should reject users without TICKET/VIEW before touching the repository")
    void shouldRejectWithoutPermission() {
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "player", false, false);
        doThrow(PermissionDeniedException.class)
                .when(adminChecker)
                .check(user, PermissionKey.TICKET, PermissionAction.VIEW);

        assertThrows(PermissionDeniedException.class, () -> useCase.execute(
                new GetTicketsInput(user, null, null, null, 0, 20, false)
        ));

        verifyNoInteractions(ticketRepository);
    }
}
