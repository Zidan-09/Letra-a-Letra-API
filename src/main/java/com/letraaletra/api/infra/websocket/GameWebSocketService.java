package com.letraaletra.api.infra.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public GameWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendGameState(String gameId, Object gameState) {
        messagingTemplate.convertAndSend(
                "/topic/game/" + gameId,
                gameState
        );
    }
}