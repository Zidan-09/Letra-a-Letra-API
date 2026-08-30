package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetMyTicketsOutput;
import com.letraaletra.api.features.ticket.domain.TicketsPage;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetMyTicketsUseCase implements UseCase<GetMyTicketsInput, GetMyTicketsOutput> {

    private final TicketRepository ticketRepository;

    public GetMyTicketsUseCase(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public GetMyTicketsOutput execute(GetMyTicketsInput input) {
        return new GetMyTicketsOutput(
                ticketRepository.findUserTickets(input.userId(), new TicketsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                ))
        );
    }
}
