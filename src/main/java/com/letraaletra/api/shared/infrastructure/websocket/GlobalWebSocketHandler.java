package com.letraaletra.api.shared.infrastructure.websocket;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorWsResponse;
import com.letraaletra.api.features.game.infrastructure.websocket.dispatcher.RoomRequestDispatcher;
import com.letraaletra.api.features.participant.infrastructure.websocket.handlers.DisconnectParticipantHandler;
import com.letraaletra.api.features.participant.infrastructure.websocket.handlers.ReconnectParticipantHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.jspecify.annotations.NonNull;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.UUID;

@Component
public class GlobalWebSocketHandler extends TextWebSocketHandler {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final RoomRequestDispatcher roomRequestDispatcher;
    private final ReconnectParticipantHandler reconnectParticipantHandler;
    private final DisconnectParticipantHandler disconnectParticipantHandler;
    private final GameNotifier gameNotifier;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    private final AuditService auditService;

    public GlobalWebSocketHandler(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            RoomRequestDispatcher roomRequestDispatcher,
            ReconnectParticipantHandler reconnectParticipantHandler,
            DisconnectParticipantHandler disconnectParticipantHandler,
            GameNotifier gameNotifier,
            JsonMapper jsonMapper,
            Validator validator,
            AuditService auditService
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.roomRequestDispatcher = roomRequestDispatcher;
        this.reconnectParticipantHandler = reconnectParticipantHandler;
        this.disconnectParticipantHandler = disconnectParticipantHandler;
        this.gameNotifier = gameNotifier;
        this.jsonMapper = jsonMapper;
        this.validator = validator;
        this.auditService = auditService;
    }

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

        WsRequest request = null;

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

            auditService.game(
                    Level.INFO,
                    "{} {} (success)",
                    user != null ? user.getNickname() : "anonymous",
                    request.getAudit()
                            .replace("Request", "")
                            .replaceAll("([a-z])([A-Z])", "$1 $2")
                            .toLowerCase()
            );

        } catch (Exception e) {
            sendError(e, user, request);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, org.springframework.web.socket.@NonNull CloseStatus status) {
        sessionRepository.remove(session);

        disconnectParticipantHandler.handler(session);
    }

    private void sendError(Exception ex, User user, WsRequest request) {
        auditService.game(
                Level.ERROR,
                "{} {} (error)",
                user != null ? user.getNickname() : "anonymous",
                request != null ? request.getAudit()
                        .replace("Request", "")
                        .replaceAll("([a-z])([A-Z])", "$1 $2")
                        .toLowerCase() : ""
        );

        Throwable cause = ex;

        if (ex instanceof java.util.concurrent.CompletionException && ex.getCause() != null) {
            cause = ex.getCause();
        }

        String message;

        if (cause instanceof DomainException appEx) {
            message = appEx.getMessage();
        } else {
            message = "an_intern_error_ocurrered";
        }

        ErrorWsResponse json = new ErrorWsResponse(message);

        if (user != null) {
            gameNotifier.notifierOne(user.getId(), json);
        }
    }
}