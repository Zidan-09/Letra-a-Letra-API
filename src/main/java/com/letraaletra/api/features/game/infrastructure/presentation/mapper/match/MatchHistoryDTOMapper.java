package com.letraaletra.api.features.game.infrastructure.presentation.mapper.match;

import com.letraaletra.api.features.game.domain.state.MatchHistory;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.match.MatchHistoryDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.match.PlayerHistoryDTO;
import com.letraaletra.api.features.player.domain.PlayerHistory;

public class MatchHistoryDTOMapper {
    public static MatchHistoryDTO toDTO(MatchHistory matchHistory) {
        return new MatchHistoryDTO(
            matchHistory.finishedAt(),
            matchHistory.players().stream()
                    .map(MatchHistoryDTOMapper::toPlayerHistoryDTO)
                    .toList()
        );
    }

    private static PlayerHistoryDTO toPlayerHistoryDTO(PlayerHistory playerHistory) {
        return new PlayerHistoryDTO(
                playerHistory.playerId().toString(),
                playerHistory.nickname(),
                playerHistory.score(),
                playerHistory.isWinner()
        );
    }
}
