package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.GetTicketByIdInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketByIdOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class GetTicketByIdMapper {

    public static GetTicketByIdInput toInput(AuthenticatedUser principal, UUID ticketId) {
        return new GetTicketByIdInput(principal, ticketId);
    }

    public static TicketResponse toResponse(GetTicketByIdOutput output) {
        return TicketResponseMapper.toResponse(output.ticket());
    }
}
