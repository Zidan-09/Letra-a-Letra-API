package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetMyTicketsOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class GetMyTicketsMapper {

    public static GetMyTicketsInput toInput(AuthenticatedUser principal, Pageable pageable) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetMyTicketsInput(
                principal.auth(),
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<TicketResponse> toResponse(GetMyTicketsOutput output) {
        return TicketResponseMapper.toPageResponse(output.tickets());
    }
}
