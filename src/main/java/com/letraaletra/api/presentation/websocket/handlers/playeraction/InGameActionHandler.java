package com.letraaletra.api.presentation.websocket.handlers.playeraction;

import com.letraaletra.api.presentation.dto.request.websocket.playeractions.PlayerActionDTO;
import org.springframework.web.socket.WebSocketSession;

public interface InGameActionHandler<T extends PlayerActionDTO> {
    void handle(T request, WebSocketSession session, String gameTokenId);

    Class<T> getType();
}
