package com.letraaletra.api.features.audit.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.letraaletra.api.features.audit.application.usecase.GetAuditEventsUseCase;
import com.letraaletra.api.features.audit.application.usecase.GetResourceAuditHistoryUseCase;
import com.letraaletra.api.features.audit.application.usecase.GetUserAuditHistoryUseCase;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.shared.application.port.AdminChecker;

@Configuration
public class AuditConfig {

    @Bean
    public GetAuditEventsUseCase getAuditEventsUseCase(FindAuditEvents findAuditEvents, AdminChecker adminChecker) {
        return new GetAuditEventsUseCase(findAuditEvents, adminChecker);
    }

    @Bean
    public GetUserAuditHistoryUseCase getUserAuditHistoryUseCase(FindAuditEvents findAuditEvents, AdminChecker adminChecker) {
        return new GetUserAuditHistoryUseCase(findAuditEvents, adminChecker);
    }

    @Bean
    public GetResourceAuditHistoryUseCase getResourceAuditHistoryUseCase(FindAuditEvents findAuditEvents, AdminChecker adminChecker) {
        return new GetResourceAuditHistoryUseCase(findAuditEvents, adminChecker);
    }
}
