package com.letraaletra.api.features.player.application.input;

import com.letraaletra.api.features.power.domain.actions.GameAction;

public record PlayerActionInput(
        String token,
        String user,
        GameAction action
) {
}
