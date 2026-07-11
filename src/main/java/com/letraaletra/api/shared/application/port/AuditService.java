package com.letraaletra.api.shared.application.port;

import org.slf4j.event.Level;

public interface AuditService {
    void admin(Level level, String message, Object... args);
    void game(Level level, String message, Object... args);
}