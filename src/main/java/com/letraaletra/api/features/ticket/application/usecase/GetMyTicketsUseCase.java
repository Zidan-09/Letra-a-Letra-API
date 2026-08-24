package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetMyTicketsOutput;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetMyTicketsUseCase implements UseCase<GetMyTicketsInput, GetMyTicketsOutput> {

    private static final int MAX_PAGE_SIZE = 200;

    private final TicketRepository ticketRepository;

    public GetMyTicketsUseCase(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public GetMyTicketsOutput execute(GetMyTicketsInput input) {
        int size = Math.min(Math.max(input.size(), 1), MAX_PAGE_SIZE);
        int page = Math.max(input.page(), 0);

        return new GetMyTicketsOutput(
                ticketRepository.findUserTickets(input.userId(), page, size, input.ascending())
        );
    }
}
