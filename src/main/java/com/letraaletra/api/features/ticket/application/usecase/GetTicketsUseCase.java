package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketsOutput;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetTicketsUseCase implements UseCase<GetTicketsInput, GetTicketsOutput> {

    private static final int MAX_PAGE_SIZE = 200;

    private final TicketRepository ticketRepository;
    private final AdminChecker adminChecker;

    public GetTicketsUseCase(TicketRepository ticketRepository, AdminChecker adminChecker) {
        this.ticketRepository = ticketRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetTicketsOutput execute(GetTicketsInput input) {
        adminChecker.check(input.principal(), PermissionKey.TICKET, PermissionAction.VIEW);

        int size = Math.min(Math.max(input.size(), 1), MAX_PAGE_SIZE);
        int page = Math.max(input.page(), 0);

        return new GetTicketsOutput(
                ticketRepository.findTickets(input.toFilter(), page, size, input.ascending())
        );
    }
}
