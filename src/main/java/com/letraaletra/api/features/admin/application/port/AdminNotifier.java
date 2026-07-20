package com.letraaletra.api.features.admin.application.port;

import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.WsAdminResponse;

public interface AdminNotifier {
    void updateConsole(WsAdminResponse dto);
    void updateMetrics(WsAdminResponse dto);
}
