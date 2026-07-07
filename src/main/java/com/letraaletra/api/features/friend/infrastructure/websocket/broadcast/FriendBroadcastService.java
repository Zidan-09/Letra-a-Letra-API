package com.letraaletra.api.features.friend.infrastructure.websocket.broadcast;

import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.FriendRequestEvent;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class FriendBroadcastService implements FriendNotifier {
    private final SessionRepository sessionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(FriendBroadcastService.class);

    public FriendBroadcastService(
            SessionRepository sessionRepository
    ) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void notifierUser(UUID userId) {
        WebSocketSession session = sessionRepository.findByUserId(userId);

        FriendRequestEvent event = new FriendRequestEvent("RECEIVE_FRIEND_REQUEST");

        try {
            String json = objectMapper.writeValueAsString(event);
            send(session, json);
        } catch (Exception e) {
            logger.warn("Error serializing message for user {}: {}", userId, e.getMessage());
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
