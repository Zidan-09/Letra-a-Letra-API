package com.letraaletra.api.infra.websocket.handlers;

import com.letraaletra.api.dto.request.websocket.PlayerActionDTO;
import org.springframework.web.socket.WebSocketSession;

public interface PlayerActionHandler {
    void handle(WebSocketSession session, PlayerActionDTO action);
}