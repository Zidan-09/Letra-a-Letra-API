package com.letraaletra.api.features.game.infrastructure.presentation.mapper.match;

import com.letraaletra.api.features.game.domain.state.MatchHistory;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.match.MatchHistoryResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.match.PlayerHistoryResponse;
import com.letraaletra.api.features.player.domain.PlayerHistory;

public class MatchHistoryResponseMapper {
    public static MatchHistoryResponse toResponse(MatchHistory matchHistory) {
        return new MatchHistoryResponse(
            matchHistory.finishedAt(),
            matchHistory.players().stream()
                    .map(MatchHistoryResponseMapper::toPlayerHistoryResponse)
                    .toList()
        );
    }

    private static PlayerHistoryResponse toPlayerHistoryResponse(PlayerHistory playerHistory) {
        return new PlayerHistoryResponse(
                playerHistory.playerId().toString(),
                playerHistory.nickname(),
                playerHistory.score(),
                playerHistory.isWinner()
        );
    }
}
