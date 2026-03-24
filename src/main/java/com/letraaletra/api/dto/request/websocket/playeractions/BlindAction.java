package com.letraaletra.api.dto.request.websocket.playeractions;

public record BlindAction(
        String targetId
) implements PlayerAction {
}
