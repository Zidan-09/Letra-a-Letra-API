package com.letraaletra.api.features.friend.infrastructure.websocket.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.FriendRequestEvent;
import com.letraaletra.api.shared.infrastructure.websocket.WsConnectionRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FriendBroadcastService implements FriendNotifier {
    private final WsConnectionRegistry connectionRegistry;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(FriendBroadcastService.class);

    @Override
    public void notifierUser(UUID userId) {
        dispatchAfterCommit(userId, "RECEIVE_FRIEND_REQUEST");
    }

    @Override
    public void notifyFriendshipAccepted(UUID userId) {
        dispatchAfterCommit(userId, "FRIEND_REQUEST_ACCEPTED");
    }

    @Override
    public void notifyFriendshipDeclined(UUID userId) {
        dispatchAfterCommit(userId, "FRIEND_REQUEST_DECLINED");
    }

    @Override
    public void notifyFriendshipRemoved(UUID userId) {
        dispatchAfterCommit(userId, "FRIENDSHIP_REMOVED");
    }

    @Override
    public void notifyFriendshipCancelled(UUID userId) {
        dispatchAfterCommit(userId, "FRIEND_REQUEST_CANCELLED");
    }

    private void dispatchAfterCommit(UUID userId, String event) {
        if (userId == null) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendEvent(userId, event);
                }
            });
            return;
        }

        sendEvent(userId, event);
    }

    private void sendEvent(UUID userId, String event) {
        WebSocketSession session = connectionRegistry.findByUserId(userId);

        try {
            String json = objectMapper.writeValueAsString(new FriendRequestEvent(event));
            send(session, json);
        } catch (Exception e) {
            logger.warn("Error serializing {} message for user {}", event, userId, e);
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
