package com.letraaletra.api.presentation.websocket;

import com.letraaletra.api.presentation.dto.request.websocket.WsRequestDTO;
import com.letraaletra.api.presentation.dto.response.websocket.ErrorWsResponse;
import com.letraaletra.api.exception.WebSocketException;
import com.letraaletra.api.domain.repository.SessionRepository;
import com.letraaletra.api.presentation.websocket.dispatcher.RoomRequestDispatcher;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RoomRequestDispatcher roomRequestDispatcher;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessionRepository.save(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        WsRequestDTO request = objectMapper.readValue(
                message.getPayload(),
                WsRequestDTO.class
        );

        try {
            roomRequestDispatcher.dispatch(request, session);

        } catch (WebSocketException ex) {
            sendError(ex, session);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, org.springframework.web.socket.@NonNull CloseStatus status) throws Exception {
        sessionRepository.remove(session);
    }

    private void sendError(Exception ex, WebSocketSession session) throws IOException {
        String json = objectMapper.writeValueAsString(new ErrorWsResponse(ex.getMessage()));

        try {
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            System.out.println("Error to send message to" + session.getId() + ": " + e.getMessage());
        }
    }
}