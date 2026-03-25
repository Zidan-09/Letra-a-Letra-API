package com.letraaletra.api.infra.websocket;

import com.letraaletra.api.dto.request.websocket.WsRequestDTO;
import com.letraaletra.api.infra.repository.SessionRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private WsRequestDispatcher wsRequestDispatcher;

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

        wsRequestDispatcher.dispatch(request, session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, org.springframework.web.socket.@NonNull CloseStatus status) throws Exception {
        sessionRepository.remove(session);
    }
}