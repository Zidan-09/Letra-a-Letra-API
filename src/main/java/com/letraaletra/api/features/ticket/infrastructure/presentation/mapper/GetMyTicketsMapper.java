package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetMyTicketsOutput;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class GetMyTicketsMapper {
    private static final Set<String> ALLOWED_SORTS =
            Set.of("createdAt", "status", "category", "subject");

    public static GetMyTicketsInput toInput(AuthenticatedUser principal, Pageable pageable) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, Sort.unsorted());

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
