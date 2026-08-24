package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.ticket.application.input.GetTicketByIdInput;
import com.letraaletra.api.features.ticket.application.output.GetTicketByIdOutput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.exception.TicketNotFoundException;
import com.letraaletra.api.features.ticket.domain.repository.FindTicket;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetTicketByIdUseCase implements UseCase<GetTicketByIdInput, GetTicketByIdOutput> {

    private final FindTicket findTicket;
    private final AdminChecker adminChecker;

    public GetTicketByIdUseCase(FindTicket findTicket, AdminChecker adminChecker) {
        this.findTicket = findTicket;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetTicketByIdOutput execute(GetTicketByIdInput input) {
        Ticket ticket = findTicket.findById(input.ticketId())
                .orElseThrow(TicketNotFoundException::new);

        if (!ticket.belongsTo(input.principal().auth())) {
            adminChecker.check(input.principal(), PermissionKey.TICKET, PermissionAction.VIEW);
        }

        return new GetTicketByIdOutput(ticket);
    }
}
