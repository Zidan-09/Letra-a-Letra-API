package com.letraaletra.api.shared.infrastructure.listener;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShutdownListener {

    private volatile boolean shuttingDown = false;

    @EventListener
    public void onClose(ContextClosedEvent event) {
        shuttingDown = true;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }
}