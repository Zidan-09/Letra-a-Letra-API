package com.letraaletra.api.features.ticket.domain.repository;

import com.letraaletra.api.features.ticket.domain.TicketDetails;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import com.letraaletra.api.features.ticket.domain.TicketsPage;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface SearchTickets {
    Page<TicketDetails> findUserTickets(UUID userId, TicketsPage page);

    Page<TicketDetails> findTickets(TicketFilter filter, TicketsPage page);

    Page<TicketDetails> findByUsername(String username, TicketsPage page);
}
