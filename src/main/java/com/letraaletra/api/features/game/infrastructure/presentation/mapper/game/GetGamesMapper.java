package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GetGamesResponse;

import java.util.List;

public class GetGamesMapper {
    public static GetGamesResponse toResponse(GetGamesOutput output) {
        List<GameDTO> games = output.games().stream()
                .map(GameDTOMapper::toDTO)
                .toList();

        return new GetGamesResponse(games);
    }
}
