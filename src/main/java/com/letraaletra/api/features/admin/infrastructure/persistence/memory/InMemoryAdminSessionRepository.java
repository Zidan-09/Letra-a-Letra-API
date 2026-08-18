package com.letraaletra.api.features.admin.infrastructure.persistence.memory;

import com.letraaletra.api.features.admin.application.port.AdminSessionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class InMemoryAdminSessionRepository implements AdminSessionRepository {
    private final Map<String, WebSocketSession> sessionsBySessionId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdByAdminId = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    @Override
    public void save(WebSocketSession session) {
        WebSocketSession decorated =
                new ConcurrentWebSocketSessionDecorator(session, 10_000, 8192);

        String sessionId = decorated.getId();
        String adminId = (String) session.getAttributes().get("adminId");

        ReentrantLock lock = lockFor(adminId);
        lock.lock();

        try {
            String oldSessionId = sessionIdByAdminId.put(adminId, sessionId);

            if (oldSessionId != null && !oldSessionId.equals(sessionId)) {
                WebSocketSession oldSession = sessionsBySessionId.remove(oldSessionId);

                if (oldSession != null) {
                    try {
                        if (oldSession.isOpen()) {
                            oldSession.close();
                        }
                    } catch (Exception ignored) {}
                }
            }

            sessionsBySessionId.put(sessionId, decorated);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<WebSocketSession> find(String sessionId) {
        return Optional.of(sessionsBySessionId.get(sessionId));
    }

    @Override
    public Optional<WebSocketSession> findByAdminId(UUID adminId) {
        String sessionId = sessionIdByAdminId.get(adminId.toString());

        if (sessionId == null) return Optional.empty();

        return Optional.of(sessionsBySessionId.get(sessionId));
    }

    @Override
    public List<WebSocketSession> get() {
        return sessionsBySessionId.values()
                .stream().toList();
    }

    @Override
    public void remove(WebSocketSession session) {
        String sessionId = session.getId();
        String adminId = (String) session.getAttributes().get("adminId");

        if (adminId == null) {
            sessionsBySessionId.remove(sessionId);
            return;
        }

        ReentrantLock lock = lockFor(adminId);
        lock.lock();

        try {
            sessionsBySessionId.remove(sessionId);

            String current = sessionIdByAdminId.get(adminId);
            if (sessionId.equals(current)) {
                sessionIdByAdminId.remove(adminId);
            }

        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock lockFor(String userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }
}
