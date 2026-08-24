package com.letraaletra.api.features.ticket.domain.repository;

import com.letraaletra.api.features.ticket.domain.Ticket;

import java.util.Optional;
import java.util.UUID;

public interface FindTicket {
    Optional<Ticket> findById(UUID ticketId);
}
