package com.letraaletra.api.features.game.domain.history;

import com.letraaletra.api.features.player.domain.PlayerHistory;

import java.time.Instant;
import java.util.List;

public record MatchHistory(
        List<PlayerHistory> players,
        List<SpectatorHistory> spectators,
        Instant finishedAt
) {
    public MatchHistory(List<PlayerHistory> players, Instant finishedAt) {
        this(players, List.of(), finishedAt);
    }
}
