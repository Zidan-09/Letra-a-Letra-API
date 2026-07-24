package com.letraaletra.api.features.game.domain.state;

import com.letraaletra.api.features.player.domain.PlayerHistory;

import java.time.Instant;
import java.util.List;

public record MatchHistory(
        List<PlayerHistory> players,
        Instant finishedAt
) {
}
