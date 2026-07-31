package com.letraaletra.api.features.player.infrastructure.websocket.dispatcher;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionRequest;
import com.letraaletra.api.features.player.infrastructure.websocket.handlers.action.InGameActionHandler;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AuditService;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PlayerActionRequestDispatcher {
    private final Map<Class<?>, InGameActionHandler<?>> handlers;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public PlayerActionRequestDispatcher(
            List<InGameActionHandler<?>> handlerList,
            UserRepository userRepository,
            AuditService auditService
    ) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(InGameActionHandler::getType, Function.identity()));
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @SuppressWarnings("unchecked")
    public <T extends PlayerActionRequest> void dispatch(
            String gameId,
            T action,
            WebSocketSession session
    ) {
        InGameActionHandler<T> handler = (InGameActionHandler<T>) handlers.get(action.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("Nenhum handler para a ação: " + action.getClass().getSimpleName());
        }

        Game game = null;
        String userDisplay = getUserDisplay(session);
        String actionType = formatActionType(action.getClass());

        try {
            game = handler.handle(action, session, gameId);
            String matchLogFileName = resolveMatchLogFileName(game);

            auditService.game(
                    gameId,
                    matchLogFileName,
                    Level.INFO,
                    "Jogador {} executou [{}] com sucesso",
                    userDisplay,
                    actionType
            );

        } catch (Exception ex) {
            String matchLogFileName = resolveMatchLogFileName(game);

            Throwable result = ex;

            while (result.getCause() != null) {
                result = result.getCause();
            }

            auditService.game(
                    gameId,
                    matchLogFileName,
                    Level.WARN,
                    "Jogador {} falhou ao executar [{}] = Exception: {}",
                    userDisplay,
                    actionType,
                    result.getMessage()
            );

            throw ex;
        }
    }

    private String resolveMatchLogFileName(Game game) {
        if (game == null || game.getGameState() == null || game.getGameState().getMatchId() == null) {
            return null;
        }

        return game.getGameState().getMatchId().toString();
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

    private String formatActionType(Class<?> clazz) {
        return clazz.getSimpleName()
                .replace("ActionRequest", "")
                .replace("Request", "")
                .toUpperCase();
    }
}