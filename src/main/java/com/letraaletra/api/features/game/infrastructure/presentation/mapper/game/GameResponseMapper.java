package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.match.MatchHistoryResponseMapper;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.MapParticipantsMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameResponseMapper {
    public static GameResponse toResponse(Game game) {
        return new GameResponse(
                game.getId().toString(),
                game.getRoomName(),
                game.getGameType(),
                game.getGameStatus(),
                MapParticipantsMapper.map(game),
                game.getParticipants().getPositions().entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().toString()
                        )),
                List.of()
        );
    }

    public static GameResponse toResponseFromHistory(GameHistory game) {
        return new GameResponse(
                game.roomId().toString(),
                game.roomName(),
                game.type(),
                game.status(),
                List.of(),
                Map.of(),
                game.matches().stream()
                        .map(MatchHistoryResponseMapper::toResponse)
                        .toList()
        );
    }
}
