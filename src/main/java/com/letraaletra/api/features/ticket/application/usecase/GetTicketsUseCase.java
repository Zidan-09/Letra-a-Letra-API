package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketsOutput;
import com.letraaletra.api.features.ticket.domain.TicketsPage;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetTicketsUseCase implements UseCase<GetTicketsInput, GetTicketsOutput> {

    private final TicketRepository ticketRepository;
    private final AdminChecker adminChecker;

    public GetTicketsUseCase(TicketRepository ticketRepository, AdminChecker adminChecker) {
        this.ticketRepository = ticketRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetTicketsOutput execute(GetTicketsInput input) {
        adminChecker.check(input.principal(), PermissionKey.TICKET, PermissionAction.VIEW);

        return new GetTicketsOutput(
                ticketRepository.findTickets(input.toFilter(), new TicketsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                ))
        );
    }
}
