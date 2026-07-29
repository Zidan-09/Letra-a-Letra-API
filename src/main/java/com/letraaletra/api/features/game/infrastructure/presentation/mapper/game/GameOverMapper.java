package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameOverDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GameOverResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerResponseMapper;

public class GameOverMapper {
    public static GameOverResponse toResponse(
            GameOver gameOver,
            Participant winnerParticipant,
            Participant loserParticipant
    ) {
        Player winner = gameOver.winner();
        Player loser = gameOver.loser();

        return new GameOverResponse(
                new GameOverDTO(
                        winner != null ? PlayerResponseMapper.toResponse(winner, winnerParticipant) : null,
                        loser != null ? PlayerResponseMapper.toResponse(loser, loserParticipant) : null
                )
        );
    }
}
