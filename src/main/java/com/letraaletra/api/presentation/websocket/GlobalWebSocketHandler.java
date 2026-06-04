package com.letraaletra.api.presentation.websocket;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.port.SessionRepository;
import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.ErrorResponse;
import com.letraaletra.api.presentation.websocket.dispatcher.RoomRequestDispatcher;
import com.letraaletra.api.features.participant.infrastructure.websocket.handlers.DisconnectParticipantHandler;
import com.letraaletra.api.features.participant.infrastructure.websocket.handlers.ReconnectParticipantHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;

@Component
public class GlobalWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RoomRequestDispatcher roomRequestDispatcher;

    @Autowired
    private ReconnectParticipantHandler reconnectParticipantHandler;

    @Autowired
    private DisconnectParticipantHandler disconnectParticipantHandler;

    @Autowired
    private GameNotifier gameNotifier;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private Validator validator;

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
        String userId = (String) session.getAttributes().get("userId");

        Throwable cause = ex;

        if (ex instanceof java.util.concurrent.CompletionException && ex.getCause() != null) {
            cause = ex.getCause();
        }

        String message;

        if (cause instanceof DomainException appEx) {
            message = appEx.getMessage();
        } else {
            message = ex.getMessage();
        }

        ErrorResponse json = new ErrorResponse(message);

        gameNotifier.notifierOne(userId, json);
    }
}