package com.letraaletra.api.features.player.domain;

import java.util.UUID;

public record PlayerHistory(
        UUID playerId,
        String nickname,
        int score,
        boolean isWinner
) {
}
