package com.letraaletra.api.shared.application.port;

import java.util.UUID;

public interface QueueChecker {
    boolean checkQueues(UUID userId);
}
