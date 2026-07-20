package com.letraaletra.api.features.admin.application.port;

import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.MetricsWsResponse;

public interface AdminNotifier {
    void updateConsole(String message);
    void updateMetrics(MetricsWsResponse dto);
}
