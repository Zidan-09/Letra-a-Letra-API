package com.letraaletra.api.features.ticket.domain.repository;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface SearchTickets {
    Page<Ticket> findUserTickets(UUID userId, int page, int size, boolean ascending);

    Page<Ticket> findTickets(TicketFilter filter, int page, int size, boolean ascending);
}
