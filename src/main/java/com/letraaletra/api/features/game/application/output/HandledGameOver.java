package com.letraaletra.api.features.game.application.output;

import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;

import java.util.Optional;

public record HandledGameOver(
        Optional<UpdateRankingPoints> winnerPoints,
        Optional<UpdateRankingPoints> loserPoints
) {
    public static HandledGameOver withoutRanking() {
        return new HandledGameOver(Optional.empty(), Optional.empty());
    }

    public static HandledGameOver withRanking(
            UpdateRankingPoints winnerPoints,
            UpdateRankingPoints loserPoints
    ) {
        return new HandledGameOver(Optional.ofNullable(winnerPoints), Optional.ofNullable(loserPoints));
    }
}
