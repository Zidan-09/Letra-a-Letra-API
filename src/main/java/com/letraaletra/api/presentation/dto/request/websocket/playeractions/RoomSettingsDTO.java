package com.letraaletra.api.presentation.dto.request.websocket.playeractions;

public record RoomSettingsDTO(
        boolean allowSpectators,
        boolean privateGame
) {
}
