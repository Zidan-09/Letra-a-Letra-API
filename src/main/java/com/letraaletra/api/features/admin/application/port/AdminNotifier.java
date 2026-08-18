package com.letraaletra.api.features.admin.application.port;

public interface AdminNotifier {
    void updateConsole(Object dto);
    void updateMetrics(Object dto);
}
