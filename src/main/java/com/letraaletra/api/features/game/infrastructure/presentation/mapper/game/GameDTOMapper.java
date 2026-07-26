package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.match.MatchHistoryDTOMapper;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.MapParticipantsMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameDTOMapper {
    public static GameDTO toDTO(Game game) {
        return new GameDTO(
                game.getId().toString(),
                game.getRoomName(),
                game.getGameType(),
                game.getGameStatus(),
                MapParticipantsMapper.execute(game),
                game.getParticipants().getPositions().entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().toString()
                        )),
                List.of()
        );
    }

    public static GameDTO toDTOFromHistory(GameHistory game) {
        return new GameDTO(
                game.roomId().toString(),
                game.roomName(),
                game.type(),
                game.status(),
                List.of(),
                Map.of(),
                game.matches().stream()
                        .map(MatchHistoryDTOMapper::toDTO)
                        .toList()
        );
    }
}
