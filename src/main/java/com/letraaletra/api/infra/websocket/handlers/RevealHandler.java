package com.letraaletra.api.infra.websocket.handlers;

import com.letraaletra.api.dto.request.websocket.PlayerActionDTO;
import com.letraaletra.api.service.GameStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

public class RevealHandler implements PlayerActionHandler {
    @Autowired
    private GameStateService gameStateService;

    @Override
    public void handle(WebSocketSession session, PlayerActionDTO action) {
        String sessionId = session.getId();

        gameStateService.revealCell("", "", action.position());
    }
}
