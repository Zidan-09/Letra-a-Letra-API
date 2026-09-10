package com.letraaletra.api.features.player.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionWsRequest;
import com.letraaletra.api.features.player.infrastructure.websocket.dispatcher.PlayerActionRequestDispatcher;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PlayerActionHandler implements RoomRequestHandler<PlayerActionWsRequest> {
    private final PlayerActionRequestDispatcher dispatcher;

    public PlayerActionHandler(
            PlayerActionRequestDispatcher dispatcher
    ) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void handle(PlayerActionWsRequest request, WebSocketSession session) {
        dispatcher.dispatch(request.gameId(), request.action(), session);
    }

    @Override
    public Class<PlayerActionWsRequest> getType() {
        return PlayerActionWsRequest.class;
    }
}
