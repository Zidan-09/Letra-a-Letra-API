package com.letraaletra.api.features.audit.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.letraaletra.api.features.audit.application.input.GetAuditEventsInput;
import com.letraaletra.api.features.audit.application.input.GetResourceAuditHistoryInput;
import com.letraaletra.api.features.audit.application.input.GetUserAuditHistoryInput;
import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
import com.letraaletra.api.features.audit.application.usecase.GetAuditEventsUseCase;
import com.letraaletra.api.features.audit.application.usecase.GetResourceAuditHistoryUseCase;
import com.letraaletra.api.features.audit.application.usecase.GetUserAuditHistoryUseCase;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;

@Configuration
public class AuditConfig {

    @Bean
    public UseCase<GetAuditEventsInput, GetAuditEventsOutput> getAuditEventsUseCase(
            FindAuditEvents findAuditEvents,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetAuditEventsUseCase(findAuditEvents, adminChecker),
                transactions
        );
    }

    @Bean
    public UseCase<GetUserAuditHistoryInput, GetAuditEventsOutput> getUserAuditHistoryUseCase(
            FindAuditEvents findAuditEvents,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetUserAuditHistoryUseCase(findAuditEvents, adminChecker),
                transactions
        );
    }

    @Bean
    public UseCase<GetResourceAuditHistoryInput, GetAuditEventsOutput> getResourceAuditHistoryUseCase(
            FindAuditEvents findAuditEvents,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetResourceAuditHistoryUseCase(findAuditEvents, adminChecker),
                transactions
        );
    }
}
