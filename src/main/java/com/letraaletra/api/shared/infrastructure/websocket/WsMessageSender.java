package com.letraaletra.api.shared.infrastructure.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@Component
public class WsMessageSender {
    private final WsConnectionRegistry connectionRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(WsMessageSender.class);

    public WsMessageSender(WsConnectionRegistry connectionRegistry) {
        this.connectionRegistry = connectionRegistry;
    }

    public void sendToSessions(Collection<String> sessionIds, Object payload) {
        final String json = encode(payload);

        if (json == null) return;

        for (String sessionId : sessionIds) {
            WebSocketSession session = connectionRegistry.find(sessionId);

            if (session == null || !session.isOpen()) continue;

            send(session, json);
        }
    }

    public void sendToUser(UUID userId, Object payload) {
        sendToUser(userId, payload, UUID.randomUUID());
    }

    public void sendToUser(UUID userId, Object payload, UUID eventId) {
        WebSocketSession session = connectionRegistry.findByUserId(userId);

        if (session == null || !session.isOpen()) return;

        String json = encode(payload, eventId.toString());

        if (json == null) return;

        send(session, json);
    }

    private String encode(Object payload) {
        return encode(payload, UUID.randomUUID().toString());
    }

    private String encode(Object payload, String eventId) {
        try {
            ObjectNode message = objectMapper.valueToTree(payload);
            message.put("eventId", eventId);

            return objectMapper.writeValueAsString(message);

        } catch (Exception e) {
            logger.warn("Error serializing broadcast message: {}", e.getMessage());
            return null;
        }
    }

    private void send(WebSocketSession session, String json) {
        if (session == null || !session.isOpen()) return;

        try {
            session.sendMessage(new TextMessage(json));

        } catch (IOException | IllegalStateException e) {
            logger.warn("Error sending message to {}: {}",
                    session.getId(),
                    e.getMessage()
            );
        }
    }
}
