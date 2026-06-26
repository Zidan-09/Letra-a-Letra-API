package com.letraaletra.api.features.matchmaking.domain;

import java.util.UUID;

public record MatchUserData(
        UUID userId,
        String session
) {
}
