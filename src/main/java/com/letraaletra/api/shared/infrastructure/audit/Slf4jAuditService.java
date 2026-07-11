package com.letraaletra.api.shared.infrastructure.audit;

import com.letraaletra.api.shared.application.port.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

@Service
public class Slf4jAuditService implements AuditService {

    private static final Logger ADMIN =
            LoggerFactory.getLogger("AUDIT_ADMIN");

    private static final Logger GAME =
            LoggerFactory.getLogger("AUDIT_GAME");

    @Override
    public void admin(Level level, String message, Object... args) {
        switch (level) {
            case INFO -> ADMIN.info(message, args);
            case WARN -> ADMIN.warn(message, args);
            case ERROR -> ADMIN.error(message, args);
            default -> ADMIN.info(message, args);
        }
    }

    @Override
    public void game(Level level, String message, Object... args) {
        switch (level) {
            case INFO -> GAME.info(message, args);
            case WARN -> GAME.warn(message, args);
            case ERROR -> GAME.error(message, args);
            default -> ADMIN.info(message, args);
        }
    }
}
