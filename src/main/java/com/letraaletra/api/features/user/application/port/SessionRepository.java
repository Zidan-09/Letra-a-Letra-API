package com.letraaletra.api.features.user.application.port;

import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public interface SessionRepository {
    void save(WebSocketSession webSocketSession);
    WebSocketSession findByUserId(UUID userId);
    WebSocketSession find(String sessionId);
    void remove(WebSocketSession webSocketSession);
}
