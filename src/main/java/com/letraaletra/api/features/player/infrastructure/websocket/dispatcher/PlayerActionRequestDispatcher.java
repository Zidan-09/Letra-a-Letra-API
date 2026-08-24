package com.letraaletra.api.features.player.infrastructure.websocket.dispatcher;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.player.domain.HandlerResult;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionRequest;
import com.letraaletra.api.features.player.infrastructure.websocket.handlers.action.InGameActionHandler;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.infrastructure.audit.MdcOperationContext;
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
    private final BusinessAuditRecorder auditRecorder;
    private final MdcOperationContext operationContext;

    public PlayerActionRequestDispatcher(
            List<InGameActionHandler<?>> handlerList,
            UserRepository userRepository,
            AuditService auditService,
            BusinessAuditRecorder auditRecorder,
            MdcOperationContext operationContext
    ) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(InGameActionHandler::getType, Function.identity()));
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.auditRecorder = auditRecorder;
        this.operationContext = operationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends PlayerActionRequest> void dispatch(
            String gameId,
            T action,
            WebSocketSession session
    ) {
        InGameActionHandler<T> handler = (InGameActionHandler<T>) handlers.get(action.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("Nenhum handler para a aÃ§Ã£o: " + action.getClass().getSimpleName());
        }

        HandlerResult handlerResult = null;
        String userDisplay = getUserDisplay(session);
        String actionType = formatActionType(action.getClass());
        String requestId = UUID.randomUUID().toString();

        operationContext.bindRequest(requestId, "WS PLAYER_ACTION " + actionType);

        try {
            handlerResult = handler.handle(action, session, gameId);

            String matchLogFileName = resolveMatchLogFileName(handlerResult.game());

            auditService.game(
                    gameId,
                    matchLogFileName,
                    Level.INFO,
                    "Jogador {} executou [{}] com sucesso",
                    userDisplay,
                    actionType
            );

            HandlerResult finalHandlerResult = handlerResult;

            finalHandlerResult.gameOver().ifPresent(over -> auditService.game(
                    finalHandlerResult.game().getId().toString(),
                    finalHandlerResult.game().getGameState().getMatchId().toString(),
                    Level.INFO,
                    "A partida acabou | Vencedor: {} ({}) - PontuaÃ§Ã£o: {} | Perdedor: {} ({}) - PontuaÃ§Ã£o: {}",
                    over.winner().getNickname(),
                    over.winner().getUserId().toString(),
                    over.winner().getScore(),
                    over.loser().getNickname(),
                    over.loser().getUserId().toString(),
                    over.loser().getScore()
            ));

        } catch (Exception ex) {
            String matchLogFileName = resolveMatchLogFileName(handlerResult != null ? handlerResult.game() : null);

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

            recordCommandFailure(session, gameId, actionType, result);

            throw ex;
        } finally {
            operationContext.unbindRequest();
        }
    }

    private void recordCommandFailure(WebSocketSession session, String gameId, String actionType, Throwable rootCause) {
        try {
            auditRecorder.recordFailure(AuditEvent.builder()
                    .category(AuditCategory.OPERATION)
                    .eventType(AuditEventType.COMMAND_FAILED)
                    .actor(sessionActor(session))
                    .resourceType(AuditResourceType.ROOM)
                    .resourceId(gameId)
                    .failureReason(rootCause.getMessage())
                    .sourceType(AuditSourceType.WEBSOCKET)
                    .metadata(Map.of(
                            "action", actionType,
                            "exceptionType", rootCause.getClass().getSimpleName()
                    ))
                    .build());
        } catch (Exception ignored) {
            // FAILURE recording is best-effort; never masks the original error.
        }
    }

    private AuditActor sessionActor(WebSocketSession session) {
        Object userIdAttr = session.getAttributes().get("userId");

        if (userIdAttr == null) {
            return new AuditActor(AuditActorType.SYSTEM, null, "ANONYMOUS");
        }

        try {
            return new AuditActor(AuditActorType.USER, UUID.fromString(userIdAttr.toString()), null);
        } catch (IllegalArgumentException ignored) {
            return new AuditActor(AuditActorType.SYSTEM, null, "ANONYMOUS");
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
                    .map(u -> String.format("%s (%s)", u.getUsername(), u.getUserId()))
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
