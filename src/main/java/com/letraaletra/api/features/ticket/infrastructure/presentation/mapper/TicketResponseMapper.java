package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.domain.TicketDetails;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public final class TicketResponseMapper {

    private TicketResponseMapper() {
    }

    public static TicketResponse toResponse(TicketDetails ticket) {
        return new TicketResponse(
                ticket.ticketId(),
                ticket.userId(),
                ticket.username(),
                ticket.category(),
                ticket.status(),
                ticket.subject(),
                ticket.description(),
                ticket.resolutionNote(),
                ticket.resolvedByAdminId(),
                ticket.adminName(),
                ticket.resolvedAt(),
                ticket.createdAt()
        );
    }

    public static PageResponse<TicketResponse> toPageResponse(Page<TicketDetails> tickets) {
        return new PageResponse<>(
                tickets.getContent().stream().map(TicketResponseMapper::toResponse).toList(),
                tickets.getNumber(),
                tickets.getSize(),
                tickets.getTotalElements(),
                tickets.getTotalPages(),
                tickets.isFirst(),
                tickets.isLast()
        );
    }
}
