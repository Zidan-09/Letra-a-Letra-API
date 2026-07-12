package com.letraaletra.api.features.admin.infrastructure.websocket.broadcast;

import com.letraaletra.api.features.admin.application.port.AdminNotifier;
import com.letraaletra.api.features.admin.application.port.AdminSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AdminBroadcastService implements AdminNotifier {
    private final AdminSessionRepository adminSessionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(AdminBroadcastService.class);

    public AdminBroadcastService(
            AdminSessionRepository adminSessionRepository
    ) {
        this.adminSessionRepository = adminSessionRepository;
    }

    @Override
    public void updateConsole(String message) {
        adminSessionRepository.get().forEach(session -> {
            if (!session.isOpen()) return;

            try {
                String json = objectMapper.writeValueAsString(message);
                send(session, json);
            } catch (Exception e) {
                logger.warn("Error serializing message for admin: {}", e.getMessage());
            }
        });
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
