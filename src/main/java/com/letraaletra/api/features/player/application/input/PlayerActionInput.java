package com.letraaletra.api.features.player.application.input;

import com.letraaletra.api.features.game.domain.board.power.actions.GameAction;

import java.util.UUID;

public record PlayerActionInput(
        String gameId,
        UUID user,
        GameAction action
) {
}
