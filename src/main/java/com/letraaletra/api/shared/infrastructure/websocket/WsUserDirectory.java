package com.letraaletra.api.shared.infrastructure.websocket;

import java.util.Optional;
import java.util.UUID;

public interface WsUserDirectory {
    boolean exists(UUID userId);

    Optional<String> describe(UUID userId);
}
