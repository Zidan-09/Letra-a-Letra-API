package com.letraaletra.api.features.game.domain.matchmaking;

import java.util.UUID;

public record MatchmakingUser(
        UUID user,
        String session
) {
}
