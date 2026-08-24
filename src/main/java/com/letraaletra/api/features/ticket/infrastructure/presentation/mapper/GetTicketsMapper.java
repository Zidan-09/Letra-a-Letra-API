package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketsOutput;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;

import java.util.UUID;

public class GetTicketsMapper {

    public static GetTicketsInput toInput(
            AuthenticatedUser principal,
            TicketStatus status,
            TicketCategory category,
            UUID userId,
            int page,
            int size,
            boolean ascending
    ) {
        return new GetTicketsInput(
                principal,
                status,
                category,
                userId,
                page,
                size,
                ascending
        );
    }

    public static PageResponse<TicketResponse> toResponse(GetTicketsOutput output) {
        return TicketResponseMapper.toPageResponse(output.tickets());
    }
}
