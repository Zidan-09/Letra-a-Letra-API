package com.letraaletra.api.shared.infrastructure.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public interface WsConnectionRegistry {
    void save(WebSocketSession webSocketSession);
    WebSocketSession findByUserId(UUID userId);
    WebSocketSession find(String sessionId);
    void remove(WebSocketSession webSocketSession);
    long playersOnline();
}
