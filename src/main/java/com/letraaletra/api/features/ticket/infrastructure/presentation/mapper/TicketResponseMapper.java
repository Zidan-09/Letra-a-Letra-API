package com.letraaletra.api.features.ticket.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.infrastructure.presentation.dto.response.ticket.TicketResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;

public final class TicketResponseMapper {

    private TicketResponseMapper() {
    }

    public static TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getTicketId(),
                ticket.getUserId(),
                ticket.getCategory(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getResolutionNote(),
                ticket.getResolvedByAdminId(),
                ticket.getResolvedAt(),
                ticket.getCreatedAt()
        );
    }

    public static PageResponse<TicketResponse> toPageResponse(Page<Ticket> tickets) {
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
