package com.letraaletra.api.application.command.game;

import com.letraaletra.api.features.game.domain.Game;

public record CloseRoomCommand(
        Game game
) {
}
