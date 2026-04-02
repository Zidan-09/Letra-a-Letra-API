package com.letraaletra.api.application.usecase.participant;

public interface DisconnectScheduler {
    void start(String userId, String gameId);
    void cancel(String userId, String gameId);
}
