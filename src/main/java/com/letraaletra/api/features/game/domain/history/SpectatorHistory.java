package com.letraaletra.api.features.game.domain.history;

import java.util.UUID;

public record SpectatorHistory(
        UUID spectatorId,
        String nickname
) {
}
