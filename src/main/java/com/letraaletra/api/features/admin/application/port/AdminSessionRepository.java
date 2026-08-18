package com.letraaletra.api.features.admin.application.port;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminSessionRepository {
    void save(WebSocketSession webSocketSession);
    Optional<WebSocketSession> findByAdminId(UUID adminId);
    Optional<WebSocketSession> find(String sessionId);
    void remove(WebSocketSession webSocketSession);
    List<WebSocketSession> get();
}
