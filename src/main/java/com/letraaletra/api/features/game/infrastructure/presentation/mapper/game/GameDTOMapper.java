package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.MapParticipantsMapper;

public class GameDTOMapper {
    public static GameDTO toDTO(Game game, String tokenGameId) {
        return new GameDTO(
                tokenGameId,
                game.getRoomName(),
                MapParticipantsMapper.execute(game),
                game.getPositions()
        );
    }
}
