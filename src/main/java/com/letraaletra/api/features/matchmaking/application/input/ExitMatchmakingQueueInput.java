package com.letraaletra.api.features.matchmaking.application.input;

import java.util.UUID;

public record ExitMatchmakingQueueInput(
        UUID userId
) {
}
