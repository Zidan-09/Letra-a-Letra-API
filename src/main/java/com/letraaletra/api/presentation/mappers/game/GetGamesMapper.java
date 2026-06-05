package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.MapParticipantsMapper;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.presentation.dto.response.game.GetGamesResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetGamesMapper {
    @Autowired
    private MapParticipantsMapper mapParticipantsMapper;

    public GetGamesResponseDTO toResponseDTO(GetGamesOutput output) {
        List<GameDTO> games = output.games().stream()
                .map(game -> toGameDTO(game, output))
                .toList();

        return new GetGamesResponseDTO(games);
    }

    private GameDTO toGameDTO(Game game, GetGamesOutput output) {
        return new GameDTO(
                output.tokens().get(game.getId()),
                game.getRoomName(),
                mapParticipantsMapper.execute(game),
                game.getPositions()
        );
    }
}
