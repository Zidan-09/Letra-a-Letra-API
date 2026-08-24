package com.letraaletra.api.shared.infrastructure.websocket;

import org.springframework.web.socket.WebSocketSession;

public interface WsLifecycleListener {
    void onConnected(WebSocketSession session);

    void onDisconnected(WebSocketSession session);
}
