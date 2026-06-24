package com.letraaletra.api.features.player.application.input;

import com.letraaletra.api.features.power.domain.actions.GameAction;

import java.util.UUID;

public record PlayerActionInput(
        String gameId,
        UUID user,
        GameAction action
) {
}
