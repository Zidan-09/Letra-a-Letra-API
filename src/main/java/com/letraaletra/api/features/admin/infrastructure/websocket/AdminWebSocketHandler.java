package com.letraaletra.api.features.admin.infrastructure.websocket;

import com.letraaletra.api.features.admin.application.port.AdminSessionRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class AdminWebSocketHandler extends TextWebSocketHandler {
    private final AdminSessionRepository adminSessionRepository;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        adminSessionRepository.save(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        adminSessionRepository.remove(session);
    }
}
