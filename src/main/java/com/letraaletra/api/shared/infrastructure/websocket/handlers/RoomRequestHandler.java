package com.letraaletra.api.shared.infrastructure.websocket.handlers;

import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import org.springframework.web.socket.WebSocketSession;

public interface RoomRequestHandler<T extends WsRequest> {
    void handle(T request, WebSocketSession session);

    Class<T> getType();
}
