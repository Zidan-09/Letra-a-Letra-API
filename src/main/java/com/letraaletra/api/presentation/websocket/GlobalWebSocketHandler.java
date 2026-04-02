package com.letraaletra.api.presentation.websocket;

import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.domain.DomainException;
import com.letraaletra.api.presentation.dto.request.WsRequestDTO;
import com.letraaletra.api.presentation.dto.response.websocket.ErrorResponseDTO;
import com.letraaletra.api.presentation.websocket.dispatcher.RoomRequestDispatcher;
import com.letraaletra.api.presentation.websocket.handlers.participant.DisconnectParticipantHandler;
import com.letraaletra.api.presentation.websocket.handlers.participant.ReconnectParticipantHandler;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

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
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessionRepository.save(session);

        reconnectParticipantHandler.handle(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        WsRequestDTO request = objectMapper.readValue(
                message.getPayload(),
                WsRequestDTO.class
        );

        try {
            roomRequestDispatcher.dispatch(request, session);

        } catch (DomainException ex) {
            sendError(ex, session);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, org.springframework.web.socket.@NonNull CloseStatus status) {
        sessionRepository.remove(session);

        disconnectParticipantHandler.handler(session);
    }

    private void sendError(Exception ex, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        String json = objectMapper.writeValueAsString(new ErrorResponseDTO(ex.getMessage()));

        gameNotifier.notifierOne(userId, json);
    }
}