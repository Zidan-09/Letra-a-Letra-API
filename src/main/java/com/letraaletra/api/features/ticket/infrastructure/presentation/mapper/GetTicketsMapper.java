package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketsOutput;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public class GetTicketsMapper {

    public static GetTicketsInput toInput(
            AuthenticatedUser principal,
            TicketStatus status,
            TicketCategory category,
            UUID userId,
            Pageable pageable
    ) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetTicketsInput(
                principal,
                status,
                category,
                userId,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<TicketResponse> toResponse(GetTicketsOutput output) {
        return TicketResponseMapper.toPageResponse(output.tickets());
    }
}
