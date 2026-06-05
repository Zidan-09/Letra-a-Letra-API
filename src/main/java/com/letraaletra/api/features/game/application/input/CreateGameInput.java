package com.letraaletra.api.features.game.application.input;

import com.letraaletra.api.features.game.domain.RoomSettings;

public record CreateGameInput(
        String name,
        RoomSettings settings,
        String session,
        String user
) {
}
