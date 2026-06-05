package com.letraaletra.api.presentation.dto.response.http;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GameDTO;

public record FindByTokenResponseDTO(
        GameDTO data
) {
}
