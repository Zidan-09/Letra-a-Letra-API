package com.letraaletra.api.shared.infrastructure.config;

import com.letraaletra.api.features.admin.application.port.AdminNotifier;
import com.letraaletra.api.shared.infrastructure.audit.WebSocketAppender;
import org.springframework.stereotype.Component;

@Component
public class LogbackInitializer {

    public LogbackInitializer(AdminNotifier notifier) {
        WebSocketAppender.setNotifier(notifier);
    }
}