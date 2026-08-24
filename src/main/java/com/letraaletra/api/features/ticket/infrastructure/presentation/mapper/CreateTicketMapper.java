package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.CreateTicketInput;
import com.letraaletra.api.features.ticket.application.output.CreateTicketOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.request.CreateTicketRequest;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.CreateTicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class CreateTicketMapper {

    public static CreateTicketInput toInput(AuthenticatedUser principal, CreateTicketRequest request) {
        return new CreateTicketInput(
                principal,
                request.category(),
                request.subject(),
                request.description()
        );
    }

    public static CreateTicketResponse toResponse(CreateTicketOutput output) {
        return new CreateTicketResponse(TicketResponseMapper.toResponse(output.ticket()));
    }
}
