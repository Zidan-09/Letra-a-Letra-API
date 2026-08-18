package com.letraaletra.api.features.game.domain.participant.port;

import java.util.UUID;

public interface DisconnectScheduler {
    void start(UUID userId, UUID gameId);
    void cancel(UUID userId, UUID gameId);
}
