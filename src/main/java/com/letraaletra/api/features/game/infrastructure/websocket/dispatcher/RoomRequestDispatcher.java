package com.letraaletra.api.features.game.infrastructure.websocket.dispatcher;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionWsRequest;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomRequestDispatcher {

    private final Map<Class<? extends WsRequest>, RoomRequestHandler<?>> handlers;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public RoomRequestDispatcher(
            List<RoomRequestHandler<?>> handlerList,
            UserRepository userRepository,
            AuditService auditService
    ) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(RoomRequestHandler::getType, Function.identity()));
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @SuppressWarnings("unchecked")
    public <T extends WsRequest> void dispatch(T request, WebSocketSession session) {
        RoomRequestHandler<T> handler = (RoomRequestHandler<T>) handlers.get(request.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("Handler not found for: " + request.getClass());
        }

        boolean isPlayerAction = request instanceof PlayerActionWsRequest;

        try {
            handler.handle(request, session);

            if (!isPlayerAction) {
                String userDisplay = getUserDisplay(session);
                String actionName = formatActionName(request.getClass().getSimpleName());
                String gameId = extractGameId(request);

                if (gameId != null) {
                    auditService.game(
                            gameId,
                            null,
                            Level.INFO,
                            "Usuário {} executou {} com sucesso",
                            userDisplay,
                            actionName
                    );
                } else {
                    auditService.game(
                            Level.INFO,
                            "Usuário {} executou {}",
                            userDisplay,
                            actionName
                    );
                }
            }

        } catch (Exception ex) {
            if (!isPlayerAction) {
                String userDisplay = getUserDisplay(session);
                String actionName = formatActionName(request.getClass().getSimpleName());
                String gameId = extractGameId(request);

                if (gameId != null) {
                    auditService.game(
                            gameId,
                            null,
                            Level.WARN,
                            "Usuário {} falhou ao executar {}: {}",
                            userDisplay,
                            actionName,
                            ex.getMessage()
                    );
                }
                throw ex;
            }
        }
    }

    private String getUserDisplay(WebSocketSession session) {
        String userIdStr = (String) session.getAttributes().get("userId");
        if (userIdStr == null) return "anonymous";

        try {
            UUID userId = UUID.fromString(userIdStr);
            return userRepository.find(userId)
                    .map(u -> String.format("%s (%s)", u.getNickname(), u.getId()))
                    .orElse(userIdStr);
        } catch (Exception e) {
            return userIdStr;
        }
    }

    private String formatActionName(String className) {
        return className
                .replace("WsRequest", "")
                .replace("Request", "")
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .toLowerCase();
    }

    private String extractGameId(Object request) {
        try {
            var method = request.getClass().getMethod("gameId");
            return (String) method.invoke(request);
        } catch (Exception ignored) {
            return null;
        }
    }
}