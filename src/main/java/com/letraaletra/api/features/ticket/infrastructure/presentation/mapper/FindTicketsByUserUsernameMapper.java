package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.FindTicketsByUserUsernameInput;
import com.letraaletra.api.features.ticket.application.output.FindTicketsByUserUsernameOutput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class FindTicketsByUserUsernameMapper {
    public static FindTicketsByUserUsernameInput toInput(AuthenticatedUser principal, String username, Pageable pageable) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new FindTicketsByUserUsernameInput(
                principal,
                username,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<TicketResponse> toResponse(FindTicketsByUserUsernameOutput output) {
        Page<Ticket> page = output.tickets();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(TicketResponseMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}