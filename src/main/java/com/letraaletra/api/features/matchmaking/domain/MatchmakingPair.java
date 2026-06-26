package com.letraaletra.api.features.matchmaking.domain;

public record MatchmakingPair(
        MatchUserData first,
        MatchUserData second
) {
}
