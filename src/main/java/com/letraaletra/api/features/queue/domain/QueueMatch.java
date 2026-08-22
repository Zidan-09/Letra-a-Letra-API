package com.letraaletra.api.features.queue.domain;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;

public record QueueMatch(
        MatchmakingPair pair,
        QueueType type
) {
}
