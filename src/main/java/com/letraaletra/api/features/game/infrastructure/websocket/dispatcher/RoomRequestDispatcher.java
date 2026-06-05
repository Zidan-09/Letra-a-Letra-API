package com.letraaletra.api.features.game.infrastructure.websocket.dispatcher;

import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoomRequestDispatcher {

    private final Map<Class<?>, RoomRequestHandler<?>> handlers = new HashMap<>();

    @Autowired
    public RoomRequestDispatcher(List<RoomRequestHandler<?>> handlerList) {
        for (RoomRequestHandler<?> handler : handlerList) {
            handlers.put(handler.getType(), handler);
        }
    }

    @SuppressWarnings("unchecked")
    public void dispatch(WsRequest request, WebSocketSession session) {
        RoomRequestHandler<WsRequest> handler =
                (RoomRequestHandler<WsRequest>) handlers.get(request.getClass());

        if (handler == null) {
            throw new InvalidPlayerActionException();
        }

        handler.handle(request, session);
    }
}
