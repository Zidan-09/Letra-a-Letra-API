package com.letraaletra.api.features.ticket.infrastructure.config;

import com.letraaletra.api.features.ticket.application.usecase.CreateTicketUseCase;
import com.letraaletra.api.features.ticket.application.usecase.GetMyTicketsUseCase;
import com.letraaletra.api.features.ticket.application.usecase.GetTicketByIdUseCase;
import com.letraaletra.api.features.ticket.application.usecase.GetTicketsUseCase;
import com.letraaletra.api.features.ticket.application.usecase.ResolveTicketUseCase;
import com.letraaletra.api.features.ticket.domain.repository.FindTicket;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketConfig {

    @Bean
    public CreateTicketUseCase createTicketUseCase(
            TicketRepository ticketRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        return new CreateTicketUseCase(ticketRepository, auditRecorder);
    }

    @Bean
    public GetMyTicketsUseCase getMyTicketsUseCase(TicketRepository ticketRepository) {
        return new GetMyTicketsUseCase(ticketRepository);
    }

    @Bean
    public GetTicketByIdUseCase getTicketByIdUseCase(FindTicket findTicket, AdminChecker adminChecker) {
        return new GetTicketByIdUseCase(findTicket, adminChecker);
    }

    @Bean
    public GetTicketsUseCase getTicketsUseCase(TicketRepository ticketRepository, AdminChecker adminChecker) {
        return new GetTicketsUseCase(ticketRepository, adminChecker);
    }

    @Bean
    public ResolveTicketUseCase resolveTicketUseCase(
            TicketRepository ticketRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder
    ) {
        return new ResolveTicketUseCase(ticketRepository, adminChecker, auditRecorder);
    }
}
