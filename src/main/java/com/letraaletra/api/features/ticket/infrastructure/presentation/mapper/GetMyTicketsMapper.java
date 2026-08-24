package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetMyTicketsOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;

public class GetMyTicketsMapper {

    public static GetMyTicketsInput toInput(AuthenticatedUser principal, int page, int size, boolean ascending) {
        return new GetMyTicketsInput(
                principal.auth(),
                page,
                size,
                ascending
        );
    }

    public static PageResponse<TicketResponse> toResponse(GetMyTicketsOutput output) {
        return TicketResponseMapper.toPageResponse(output.tickets());
    }
}
