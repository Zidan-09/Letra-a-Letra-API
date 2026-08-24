package com.letraaletra.api.features.ticket.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.ticket.application.input.CreateTicketInput;
import com.letraaletra.api.features.ticket.application.output.CreateTicketOutput;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class CreateTicketUseCase implements UseCase<CreateTicketInput, CreateTicketOutput> {

    private final TicketRepository ticketRepository;
    private final BusinessAuditRecorder auditRecorder;

    public CreateTicketUseCase(TicketRepository ticketRepository, BusinessAuditRecorder auditRecorder) {
        this.ticketRepository = ticketRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public CreateTicketOutput execute(CreateTicketInput input) {
        Ticket ticket = Ticket.create(
                input.principal().auth(),
                input.category(),
                input.subject(),
                input.description()
        );

        ticketRepository.save(ticket);

        auditRecorder.record(AuditEventFactory.ticketCreated(
                ticket,
                actor(input.principal()),
                UUID.randomUUID()
        ));

        return new CreateTicketOutput(ticket);
    }

    private AuditActor actor(AuthenticatedUser principal) {
        AuditActorType type = principal.isAdmin() ? AuditActorType.ADMIN : AuditActorType.USER;

        return new AuditActor(type, principal.auth(), principal.name());
    }
}
