package com.letraaletra.api.features.player.application.input;

import java.util.UUID;

public record DiscardPowerInput(
        String gameId,
        UUID userId,
        String powerId
) {
}
