package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;

public record FindByTokenResponse(
        GameDTO data
) {
}
