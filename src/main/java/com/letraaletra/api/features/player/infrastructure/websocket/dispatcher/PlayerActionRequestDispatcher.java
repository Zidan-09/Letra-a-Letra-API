package com.letraaletra.api.features.player.infrastructure.websocket.dispatcher;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionRequest;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;
import com.letraaletra.api.features.player.infrastructure.websocket.handlers.action.InGameActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerActionRequestDispatcher {
    private final Map<Class<?>, InGameActionHandler<?>> handlers = new HashMap<>();

    @Autowired
    public PlayerActionRequestDispatcher(List<InGameActionHandler<?>> handlerList) {
        for (InGameActionHandler<?> handler : handlerList) {
            handlers.put(handler.getType(), handler);
        }
    }

    @SuppressWarnings("unchecked")
    public void dispatch(String gameTokenId, PlayerActionRequest request, WebSocketSession session) {
        InGameActionHandler<PlayerActionRequest> handler =
                (InGameActionHandler<PlayerActionRequest>) handlers.get(request.getClass());

        if (handler == null) {
            throw new InvalidPlayerActionException();
        }

        handler.handle(request, session, gameTokenId);
    }
}
