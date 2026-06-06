package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;

import java.util.List;

public record GetGamesResponse(
        List<GameDTO> games
) {
}
