package com.letraaletra.api.features.game.application.input;

import com.letraaletra.api.features.game.domain.Game;

public record CloseRoomInput(
        Game game
) {
}
