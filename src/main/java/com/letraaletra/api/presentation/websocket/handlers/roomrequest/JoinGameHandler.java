package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.game.usecase.JoinGameUseCase;
import com.letraaletra.api.presentation.dto.request.websocket.JoinGameWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class JoinGameHandler implements RoomRequestHandler<JoinGameWsRequest> {
    @Autowired
    private JoinGameUseCase joinGame;

    @Override
    public void handle(JoinGameWsRequest request, WebSocketSession session) {
        joinGame.execute(
                request.tokenGameId(),
                session.getId(),
                (String) session.getAttributes().get("userId")
        );
    }

    @Override
    public Class<JoinGameWsRequest> getType() {
        return JoinGameWsRequest.class;
    }
}

