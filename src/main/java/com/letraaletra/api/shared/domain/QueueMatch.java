package com.letraaletra.api.shared.domain;

import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;

public record QueueMatch(
        MatchmakingPair pair,
        GameMode mode,
        QueueType type
) {
}
