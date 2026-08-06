package com.letraaletra.api.shared.infrastructure.websocket;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.infrastructure.listener.ShutdownListener;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorWsResponse;
import com.letraaletra.api.features.game.infrastructure.websocket.dispatcher.RoomRequestDispatcher;
import com.letraaletra.api.features.participant.infrastructure.websocket.handlers.DisconnectParticipantHandler;
import com.letraaletra.api.features.participant.infrastructure.websocket.handlers.ReconnectParticipantHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
public class GlobalWebSocketHandler extends TextWebSocketHandler {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final RoomRequestDispatcher roomRequestDispatcher;
    private final ReconnectParticipantHandler reconnectParticipantHandler;
    private final DisconnectParticipantHandler disconnectParticipantHandler;
    private final GameNotifier gameNotifier;
    private final JsonMapper jsonMapper;
    private final Validator validator;
    private final ShutdownListener shutdownListener;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessionRepository.save(session);

        reconnectParticipantHandler.handle(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        String userId = (String) session.getAttributes().get("userId");

        User user = userRepository.find(UUID.fromString(userId))
                .orElse(null);

        WsRequest request;

        try {
            request = jsonMapper.readValue(
                    message.getPayload(),
                    WsRequest.class
            );

            Set<ConstraintViolation<WsRequest>> violations = validator.validate(request);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            roomRequestDispatcher.dispatch(request, session);

        } catch (Exception e) {
            sendError(e, user);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {

        if (shutdownListener.isShuttingDown()) {
            return;
        }

        sessionRepository.remove(session);

        disconnectParticipantHandler.handler(session);
    }

    private void sendError(Exception ex, User user) {

        Throwable cause = ex;

        if (ex instanceof CompletionException && ex.getCause() != null) {
            cause = ex.getCause();
        }

        String message;

        if (cause instanceof DomainException appEx) {
            message = appEx.getMessage();
        } else {
            message = "an unexpected internal server error occurred";
        }

        ErrorWsResponse json = new ErrorWsResponse(message);

        if (user != null) {
            gameNotifier.notifierOne(user.getUserId(), json);
        }
    }
}