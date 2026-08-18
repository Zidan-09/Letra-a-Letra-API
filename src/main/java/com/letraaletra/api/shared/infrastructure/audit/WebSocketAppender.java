package com.letraaletra.api.shared.infrastructure.audit;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.letraaletra.api.features.admin.application.port.AdminNotifier;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.LogWsResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.LogMapper;

public class WebSocketAppender extends AppenderBase<ILoggingEvent> {
    private static AdminNotifier notifier;

    public static void setNotifier(AdminNotifier adminNotifier) {
        notifier = adminNotifier;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (notifier == null) {
            return;
        }

        PatternLayout layout = new PatternLayout();
        layout.setContext(getContext());
        layout.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger - %msg%n");
        layout.start();

        String text = layout.doLayout(event);

        LogWsResponse dto = LogMapper.toResponse(text);

        notifier.updateConsole(dto);
    }
}
