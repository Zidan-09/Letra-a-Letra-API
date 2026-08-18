package com.letraaletra.api.shared.application.port;

import jakarta.annotation.Nullable;
import org.slf4j.event.Level;

public interface AuditService {
    void admin(Level level, String message, Object... args);
    void game(Level level, String message, Object... args);
    void game(String gameId, @Nullable String matchId, Level level, String message, Object... args);
}