package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.MapParticipantsMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameDTOMapper {
    public static GameDTO toDTO(Game game, String tokenGameId) {
        return new GameDTO(
                tokenGameId,
                game.getRoomName(),
                MapParticipantsMapper.execute(game),
                game.getPositions().entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().toString()
                        ))
        );
    }
}
