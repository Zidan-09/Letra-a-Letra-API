package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameOverDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GameOverResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerDTOMapper;

public class GameOverMapper {
    public static GameOverResponse toResponse(GameOverResult gameOverResult, Game game) {
        Player winner = gameOverResult.winner();
        Player loser = gameOverResult.loser();

        return new GameOverResponse(
                new GameOverDTO(
                        winner != null ? PlayerDTOMapper.toDTO(winner, game.getParticipantByUserId(winner.getUserId())) : null,
                        loser != null ? PlayerDTOMapper.toDTO(loser, game.getParticipantByUserId(loser.getUserId())) : null
                )
        );
    }
}
