package com.letraaletra.api.shared.domain.repository;

import java.util.UUID;

public interface UserIsOnQueue {
    boolean onQueue(UUID userId);
}
