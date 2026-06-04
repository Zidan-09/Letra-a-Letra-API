package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import org.springframework.web.socket.WebSocketSession;

public interface RoomRequestHandler<T extends WsRequest> {
    void handle(T request, WebSocketSession session);

    Class<T> getType();
}
