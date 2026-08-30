package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.ticket.application.input.FindTicketsByUserUsernameInput;
import com.letraaletra.api.features.ticket.application.output.FindTicketsByUserUsernameOutput;
import com.letraaletra.api.features.ticket.domain.TicketsPage;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class FindTicketsByUserUsernameUseCase implements UseCase<FindTicketsByUserUsernameInput, FindTicketsByUserUsernameOutput> {

    private final TicketRepository ticketRepository;
    private final AdminChecker adminChecker;

    public FindTicketsByUserUsernameUseCase(TicketRepository ticketRepository, AdminChecker adminChecker) {
        this.ticketRepository = ticketRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public FindTicketsByUserUsernameOutput execute(FindTicketsByUserUsernameInput input) {
        adminChecker.check(input.principal(), PermissionKey.TICKET, PermissionAction.VIEW);

        return new FindTicketsByUserUsernameOutput(
                ticketRepository.findByUsername(input.username(), new TicketsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                ))
        );
    }
}