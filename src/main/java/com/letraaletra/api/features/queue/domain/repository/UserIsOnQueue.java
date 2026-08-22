package com.letraaletra.api.features.queue.domain.repository;

import java.util.UUID;

public interface UserIsOnQueue {
    boolean onQueue(UUID userId);
}
