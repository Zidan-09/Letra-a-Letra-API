package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.game.usecase.LeftGameUseCase;
import com.letraaletra.api.presentation.dto.request.websocket.LeftGameWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LeftGameHandler implements RoomRequestHandler<LeftGameWsRequest> {
    @Autowired
    private LeftGameUseCase leftGame;

    @Override
    public void handle(LeftGameWsRequest request, WebSocketSession session) {
        leftGame.execute(
                request.tokenGameId(),
                session.getId()
        );
    }

    @Override
    public Class<LeftGameWsRequest> getType() {
        return LeftGameWsRequest.class;
    }
}
