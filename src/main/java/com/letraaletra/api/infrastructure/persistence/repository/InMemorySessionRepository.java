package com.letraaletra.api.infrastructure.persistence.repository;

import com.letraaletra.api.infrastructure.websocket.SessionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySessionRepository implements SessionRepository {

    private final Map<String, WebSocketSession> sessionsBySessionId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdByUserId = new ConcurrentHashMap<>();

    @Override
    public void save(WebSocketSession session) {
        String sessionId = session.getId();
        String userId = (String) session.getAttributes().get("userId");

        String existingSessionId = sessionIdByUserId.get(userId);

        if (existingSessionId != null) {
            WebSocketSession oldSession = sessionsBySessionId.get(existingSessionId);

            if (oldSession != null && oldSession.isOpen()) {
                try {
                    oldSession.close();
                } catch (Exception ignored) {}
            }

            sessionsBySessionId.remove(existingSessionId);
        }

        sessionsBySessionId.put(sessionId, session);
        sessionIdByUserId.put(userId, sessionId);
    }

    @Override
    public WebSocketSession find(String sessionId) {
        return sessionsBySessionId.get(sessionId);
    }

    public WebSocketSession findByUserId(String userId) {
        String sessionId = sessionIdByUserId.get(userId);

        if (sessionId == null) return null;

        return sessionsBySessionId.get(sessionId);
    }

    @Override
    public void remove(WebSocketSession session) {
        String sessionId = session.getId();
        String userId = (String) session.getAttributes().get("userId");

        sessionsBySessionId.remove(sessionId);

        if (userId != null) {
            sessionIdByUserId.remove(userId);
        }
    }
}