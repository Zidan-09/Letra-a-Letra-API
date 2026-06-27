package com.letraaletra.api.shared.infrastructure.websocket;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.user.application.port.SessionRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.UUID;

@Component
public class GlobalWebSocketHandler extends TextWebSocketHandler {
    private final SessionRepository sessionRepository;
    private final RoomRequestDispatcher roomRequestDispatcher;
    private final ReconnectParticipantHandler reconnectParticipantHandler;
    private final DisconnectParticipantHandler disconnectParticipantHandler;
    private final GameNotifier gameNotifier;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    private final Logger logger = LoggerFactory.getLogger(GlobalWebSocketHandler.class);

    public GlobalWebSocketHandler(
            SessionRepository sessionRepository,
            RoomRequestDispatcher roomRequestDispatcher,
            ReconnectParticipantHandler reconnectParticipantHandler,
            DisconnectParticipantHandler disconnectParticipantHandler,
            GameNotifier gameNotifier,
            JsonMapper jsonMapper,
            Validator validator
    ) {
        this.sessionRepository = sessionRepository;
        this.roomRequestDispatcher = roomRequestDispatcher;
        this.reconnectParticipantHandler = reconnectParticipantHandler;
        this.disconnectParticipantHandler = disconnectParticipantHandler;
        this.gameNotifier = gameNotifier;
        this.jsonMapper = jsonMapper;
        this.validator = validator;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessionRepository.save(session);

        reconnectParticipantHandler.handle(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            WsRequest request = jsonMapper.readValue(
                    message.getPayload(),
                    WsRequest.class
            );

            Set<ConstraintViolation<WsRequest>> violations = validator.validate(request);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            roomRequestDispatcher.dispatch(request, session);

        } catch (Exception e ) {
            sendError(e, session);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, org.springframework.web.socket.@NonNull CloseStatus status) {
        sessionRepository.remove(session);

        disconnectParticipantHandler.handler(session);
    }

    private void sendError(Exception ex, WebSocketSession session) {
        logger.error("\nAn exception was threw {}\n", ex.getMessage());

        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

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

        gameNotifier.notifierOne(userId, json);
    }
}