package com.letraaletra.api.features.game.application.port;

public interface DisconnectScheduler {
    void start(String userId, String gameId);
    void cancel(String userId, String gameId);
}
