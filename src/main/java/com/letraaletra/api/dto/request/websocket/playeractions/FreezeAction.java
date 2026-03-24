package com.letraaletra.api.dto.request.websocket.playeractions;

public record FreezeAction(
        String targetId
) implements PlayerAction {
}
