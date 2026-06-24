package com.letraaletra.api.features.matchmaking.domain.repository;

import java.util.UUID;

public interface UserIsOnQueue {
    boolean onQueue(UUID userId);
}
