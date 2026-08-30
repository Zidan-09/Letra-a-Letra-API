package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMyTicketsUseCaseTest {

    @Mock
    private TicketRepository ticketRepository;

    private GetMyTicketsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetMyTicketsUseCase(ticketRepository);
    }

    @Test
    @DisplayName("Should query tickets strictly by the user id resolved from the principal")
    void shouldQueryByPrincipalUserId() {
        UUID userId = UUID.randomUUID();
        when(ticketRepository.findUserTickets(eq(userId), any()))
                .thenReturn(Page.empty());

        useCase.execute(new GetMyTicketsInput(userId, 0, 20, Sort.unsorted()));

        verify(ticketRepository).findUserTickets(eq(userId), any());
    }

    @Test
    @DisplayName("Should return the page produced by the repository")
    void shouldReturnRepositoryPage() {
        UUID userId = UUID.randomUUID();
        Ticket ticket = Ticket.create(
                userId,
                TicketCategory.OTHER,
                "General doubt",
                "How does the matchmaking rating work exactly?"
        );
        Page<Ticket> page = new PageImpl<>(List.of(ticket));
        when(ticketRepository.findUserTickets(eq(userId), any()))
                .thenReturn(page);

        var output = useCase.execute(new GetMyTicketsInput(userId, 0, 20, Sort.unsorted()));

        assertEquals(1, output.tickets().getContent().size());
        assertEquals(ticket.getTicketId(), output.tickets().getContent().get(0).getTicketId());
    }
}
