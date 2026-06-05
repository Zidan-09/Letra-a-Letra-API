package com.letraaletra.api.features.game.application.input;

import com.letraaletra.api.features.game.domain.state.GameSettings;

public record StartGameInput(
        String token,
        String session,
        GameSettings settings
) {
}
