package com.letraaletra.api.shared.infrastructure.audit;

import com.letraaletra.api.shared.application.port.AuditService;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class Slf4jAuditService implements AuditService {

    private static final Logger ADMIN = LoggerFactory.getLogger("AUDIT_ADMIN");
    private static final Logger GAME = LoggerFactory.getLogger("AUDIT_GAME");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void admin(Level level, String message, Object... args) {
        logTo(ADMIN, level, message, args);
    }

    @Override
    public void game(Level level, String message, Object... args) {
        logTo(GAME, level, message, args);
    }

    @Override
    public void game(String gameId, @Nullable String matchId, Level level, String message, Object... args) {
        String today = LocalDate.now().format(DATE_FORMATTER);

        String relativePath;
        if (matchId != null && !matchId.isBlank()) {
            relativePath = String.format("%s/%s/%s.log", today, gameId, "match-" + matchId);
        } else {
            relativePath = String.format("%s/%s/game.log", today, gameId);
        }

        try {
            MDC.put("gameLogPath", relativePath);
            logTo(GAME, level, message, args);
        } finally {
            MDC.remove("gameLogPath");
        }
    }

    private void logTo(Logger logger, Level level, String message, Object... args) {
        switch (level) {
            case INFO -> logger.info(message, args);
            case WARN -> logger.warn(message, args);
            case ERROR -> logger.error(message, args);
            default -> logger.info(message, args);
        }
    }
}
