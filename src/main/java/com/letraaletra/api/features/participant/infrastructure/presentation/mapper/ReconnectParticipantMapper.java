package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameResponseMapper;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateResponseMapper;
import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;

import java.util.UUID;

public class ReconnectParticipantMapper {
    public static ReconnectParticipantInput toInput(String user, String session) {
        return new ReconnectParticipantInput(
                UUID.fromString(user),
                session
        );
    }

    public static ReconnectParticipantResponse toResponse(ReconnectParticipantOutput output) {
        Game game = output.game();
        GameResponse room = GameResponseMapper.toResponse(game);

        if (game.getGameStatus() == GameStatus.RUNNING) {
            GameStateResponse gameState = GameStateResponseMapper.toGlobalResponse(game);
            return new ReconnectParticipantResponse(room, gameState);
        }

        return new ReconnectParticipantResponse(room, null);
    }
}
