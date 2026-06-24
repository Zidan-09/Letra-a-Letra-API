package com.letraaletra.api.features.game.application.port;

import java.util.UUID;

public interface DisconnectScheduler {
    void start(UUID userId, String gameId);
    void cancel(UUID userId, String gameId);
}
