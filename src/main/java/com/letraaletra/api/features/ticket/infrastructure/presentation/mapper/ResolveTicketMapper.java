package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.ResolveTicketInput;
import com.letraaletra.api.features.ticket.application.output.ResolveTicketOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request.ResolveTicketRequest;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ResolveTicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class ResolveTicketMapper {

    public static ResolveTicketInput toInput(
            AuthenticatedUser principal,
            UUID ticketId,
            ResolveTicketRequest request
    ) {
        return new ResolveTicketInput(
                principal,
                ticketId,
                request == null ? null : request.resolutionNote()
        );
    }

    public static ResolveTicketResponse toResponse(ResolveTicketOutput output) {
        return new ResolveTicketResponse(TicketResponseMapper.toResponse(output.ticket()));
    }
}
