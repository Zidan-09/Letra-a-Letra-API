package com.letraaletra.api.features.admin.infrastructure.logging;

import com.letraaletra.api.features.admin.application.port.AdminNotifier;
import com.letraaletra.api.features.admin.infrastructure.logging.WebSocketAppender;
import org.springframework.stereotype.Component;

@Component
public class LogbackInitializer {

    public LogbackInitializer(AdminNotifier notifier) {
        WebSocketAppender.setNotifier(notifier);
    }
}