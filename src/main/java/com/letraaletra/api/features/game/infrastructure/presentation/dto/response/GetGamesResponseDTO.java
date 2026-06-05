package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import java.util.List;

public record GetGamesResponseDTO(
        List<GameDTO> games
) {
}
