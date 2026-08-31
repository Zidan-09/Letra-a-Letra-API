package com.letraaletra.api.features.ticket.domain.repository;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketDetails;

import java.util.Optional;
import java.util.UUID;

public interface FindTicket {
    Optional<TicketDetails> findDetailsById(UUID ticketId);
    Optional<Ticket> findById(UUID ticketId);
}
