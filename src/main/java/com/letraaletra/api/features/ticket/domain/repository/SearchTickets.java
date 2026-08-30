package com.letraaletra.api.features.ticket.domain.repository;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import com.letraaletra.api.features.ticket.domain.TicketsPage;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface SearchTickets {
    Page<Ticket> findUserTickets(UUID userId, TicketsPage page);

    Page<Ticket> findTickets(TicketFilter filter, TicketsPage page);

    Page<Ticket> findByUsername(String username, TicketsPage page);
}
