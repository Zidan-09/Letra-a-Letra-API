package com.letraaletra.api.features.admin.infrastructure.scheduler;

import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.application.port.AdminNotifier;
import com.letraaletra.api.features.admin.application.service.GetApplicationStatusService;
import com.letraaletra.api.features.admin.application.service.GetSystemStatusService;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.MetricsWsResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.MetricsMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricsScheduler {
    private final GetSystemStatusService systemStatusService;
    private final GetApplicationStatusService applicationStatusService;
    private final AdminNotifier notifier;

    public MetricsScheduler(
            GetSystemStatusService systemStatusService,
            GetApplicationStatusService applicationStatusService,
            AdminNotifier notifier
    ) {
        this.systemStatusService = systemStatusService;
        this.applicationStatusService = applicationStatusService;
        this.notifier = notifier;
    }

    @Scheduled(fixedRate = 1000)
    public void processMetrics() {
        GetSystemStatusOutput systemStatusOutput = systemStatusService.handle();
        GetApplicationStatusOutput applicationStatusOutput = applicationStatusService.handle();

        MetricsWsResponse dto = MetricsMapper.toResponse(systemStatusOutput, applicationStatusOutput);

        notifier.updateMetrics(dto);
    }
}
