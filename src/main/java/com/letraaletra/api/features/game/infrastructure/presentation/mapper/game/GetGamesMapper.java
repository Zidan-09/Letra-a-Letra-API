package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.MapParticipantsMapper;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GetGamesResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetGamesMapper {
    public static GetGamesResponse toResponseDTO(GetGamesOutput output) {
        List<GameDTO> games = output.games().stream()
                .map(game -> toGameDTO(game, output))
                .toList();

        return new GetGamesResponse(games);
    }

    private static GameDTO toGameDTO(Game game, GetGamesOutput output) {
        return new GameDTO(
                output.tokens().get(game.getId()),
                game.getRoomName(),
                MapParticipantsMapper.execute(game),
                game.getPositions()
        );
    }
}
