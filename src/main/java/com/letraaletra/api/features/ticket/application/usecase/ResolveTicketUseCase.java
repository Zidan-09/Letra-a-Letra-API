package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.ticket.application.input.ResolveTicketInput;
import com.letraaletra.api.features.ticket.application.output.ResolveTicketOutput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.domain.exception.TicketNotFoundException;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class ResolveTicketUseCase implements UseCase<ResolveTicketInput, ResolveTicketOutput> {

    private final TicketRepository ticketRepository;
    private final AdminChecker adminChecker;
    private final BusinessAuditRecorder auditRecorder;

    public ResolveTicketUseCase(
            TicketRepository ticketRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder
    ) {
        this.ticketRepository = ticketRepository;
        this.adminChecker = adminChecker;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public ResolveTicketOutput execute(ResolveTicketInput input) {
        adminChecker.check(input.principal(), PermissionKey.TICKET, PermissionAction.EDIT);

        Ticket ticket = ticketRepository.findById(input.ticketId())
                .orElseThrow(TicketNotFoundException::new);

        TicketStatus previousStatus = ticket.getStatus();

        ticket.resolve(input.principal().auth(), input.resolutionNote());

        ticketRepository.save(ticket);

        auditRecorder.record(AuditEventFactory.ticketResolved(
                previousStatus,
                ticket,
                actor(input.principal()),
                UUID.randomUUID()
        ));

        return new ResolveTicketOutput(ticket);
    }

    private AuditActor actor(AuthenticatedUser principal) {
        AuditActorType type = principal.isAdmin() ? AuditActorType.ADMIN : AuditActorType.USER;

        return new AuditActor(type, principal.auth(), principal.name());
    }
}
