package com.letraaletra.api.features.ticket.infrastructure.config;

import com.letraaletra.api.features.ticket.application.input.CreateTicketInput;
import com.letraaletra.api.features.ticket.application.input.FindTicketsByUserUsernameInput;
import com.letraaletra.api.features.ticket.application.input.GetMyTicketsInput;
import com.letraaletra.api.features.ticket.application.input.GetTicketByIdInput;
import com.letraaletra.api.features.ticket.application.input.GetTicketsInput;
import com.letraaletra.api.features.ticket.application.input.ResolveTicketInput;
import com.letraaletra.api.features.ticket.application.output.CreateTicketOutput;
import com.letraaletra.api.features.ticket.application.output.FindTicketsByUserUsernameOutput;
import com.letraaletra.api.features.ticket.application.output.GetMyTicketsOutput;
import com.letraaletra.api.features.ticket.application.output.GetTicketByIdOutput;
import com.letraaletra.api.features.ticket.application.output.GetTicketsOutput;
import com.letraaletra.api.features.ticket.application.output.ResolveTicketOutput;
import com.letraaletra.api.features.ticket.application.usecase.CreateTicketUseCase;
import com.letraaletra.api.features.ticket.application.usecase.FindTicketsByUserUsernameUseCase;
import com.letraaletra.api.features.ticket.application.usecase.GetMyTicketsUseCase;
import com.letraaletra.api.features.ticket.application.usecase.GetTicketByIdUseCase;
import com.letraaletra.api.features.ticket.application.usecase.GetTicketsUseCase;
import com.letraaletra.api.features.ticket.application.usecase.ResolveTicketUseCase;
import com.letraaletra.api.features.ticket.domain.repository.FindTicket;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketConfig {

    @Bean
    public UseCase<CreateTicketInput, CreateTicketOutput> createTicketUseCase(
            TicketRepository ticketRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new CreateTicketUseCase(ticketRepository, auditRecorder),
                transactions
        );
    }

    @Bean
    public UseCase<GetMyTicketsInput, GetMyTicketsOutput> getMyTicketsUseCase(
            TicketRepository ticketRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetMyTicketsUseCase(ticketRepository),
                transactions
        );
    }

    @Bean
    public UseCase<GetTicketByIdInput, GetTicketByIdOutput> getTicketByIdUseCase(
            FindTicket findTicket,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetTicketByIdUseCase(findTicket, adminChecker),
                transactions
        );
    }

    @Bean
    public UseCase<GetTicketsInput, GetTicketsOutput> getTicketsUseCase(
            TicketRepository ticketRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetTicketsUseCase(ticketRepository, adminChecker),
                transactions
        );
    }

    @Bean
    public UseCase<FindTicketsByUserUsernameInput, FindTicketsByUserUsernameOutput> findTicketsByUserUsernameUseCase(
            TicketRepository ticketRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindTicketsByUserUsernameUseCase(ticketRepository, adminChecker),
                transactions
        );
    }

    @Bean
    public UseCase<ResolveTicketInput, ResolveTicketOutput> resolveTicketUseCase(
            TicketRepository ticketRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ResolveTicketUseCase(ticketRepository, adminChecker, auditRecorder),
                transactions
        );
    }
}
