package com.letraaletra.api.application.command.game;

import com.letraaletra.api.domain.game.GameSettings;

public record StartGameCommand(
        String token,
        String session,
        GameSettings settings
) {
}
