package com.letraaletra.api.features.matchmaking.domain;

import com.letraaletra.api.features.queue.domain.OnlineUser;

public record MatchmakingPair(
        OnlineUser first,
        OnlineUser second
) {
}
