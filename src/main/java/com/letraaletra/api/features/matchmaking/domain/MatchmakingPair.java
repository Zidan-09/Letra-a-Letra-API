package com.letraaletra.api.features.matchmaking.domain;

import com.letraaletra.api.shared.domain.OnlineUser;

public record MatchmakingPair(
        OnlineUser first,
        OnlineUser second
) {
}
