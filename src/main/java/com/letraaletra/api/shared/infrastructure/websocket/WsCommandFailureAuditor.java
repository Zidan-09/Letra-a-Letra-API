package com.letraaletra.api.shared.infrastructure.websocket;

import org.springframework.web.socket.WebSocketSession;

public interface WsCommandFailureAuditor {
    void recordCommandFailure(WebSocketSession session, String gameId, Throwable rootCause);
}
