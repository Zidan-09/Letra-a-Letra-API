package com.letraaletra.api.features.game.application.input;

import com.letraaletra.api.features.game.domain.state.GameSettings;

import java.util.UUID;

public record StartGameInput(
        UUID gameId,
        String session,
        GameSettings settings
) {
}
