package com.letraaletra.api.features.game.application.input;

import com.letraaletra.api.features.game.domain.RoomSettings;

import java.util.UUID;

public record CreateGameInput(
        String name,
        RoomSettings settings,
        String session,
        UUID user
) {
}
