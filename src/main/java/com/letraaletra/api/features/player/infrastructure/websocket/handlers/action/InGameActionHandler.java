package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionRequest;
import org.springframework.web.socket.WebSocketSession;

public interface InGameActionHandler<T extends PlayerActionRequest> {
    void handle(T request, WebSocketSession session, String gameId);

    Class<T> getType();
}
