package com.letraaletra.api.features.game.infrastructure.websocket.dispatcher;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionWsRequest;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.infrastructure.audit.MdcOperationContext;
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
    private final BusinessAuditRecorder auditRecorder;
    private final MdcOperationContext operationContext;

    public RoomRequestDispatcher(
            List<RoomRequestHandler<?>> handlerList,
            UserRepository userRepository,
            AuditService auditService,
            BusinessAuditRecorder auditRecorder,
            MdcOperationContext operationContext
    ) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(RoomRequestHandler::getType, Function.identity()));
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.auditRecorder = auditRecorder;
        this.operationContext = operationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends WsRequest> void dispatch(T request, WebSocketSession session) {
        RoomRequestHandler<T> handler = (RoomRequestHandler<T>) handlers.get(request.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("Handler not found for: " + request.getClass());
        }

        boolean isPlayerAction = request instanceof PlayerActionWsRequest;
        String requestId = UUID.randomUUID().toString();
        String actionName = formatActionName(request.getClass().getSimpleName());
        String gameId = extractGameId(request);
        String userDisplay = getUserDisplay(session);

        operationContext.bindRequest(requestId, "WS " + actionName);

        try {
            handler.handle(request, session);

            if (!isPlayerAction) {
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
                Throwable result = ex;

                while (result.getCause() != null) {
                    result = result.getCause();
                }

                if (gameId != null) {
                    auditService.game(
                            gameId,
                            null,
                            Level.WARN,
                            "Usuário {} falhou ao executar {} = Exception: {}",
                            userDisplay,
                            actionName,
                            result.getMessage()
                    );
                } else {
                    auditService.game(
                            Level.WARN,
                            "Usuário {} falhou ao executar {} = Exception: {}",
                            userDisplay,
                            actionName,
                            result.getMessage()
                    );
                }

                recordCommandFailure(session, gameId, result);
            }

            throw ex;
        } finally {
            operationContext.unbindRequest();
        }
    }

    private void recordCommandFailure(WebSocketSession session, String gameId, Throwable rootCause) {
        try {
            AuditResourceType resourceType = gameId != null
                    ? AuditResourceType.ROOM
                    : AuditResourceType.USER;

            String resourceId = gameId != null
                    ? gameId
                    : String.valueOf(session.getAttributes().getOrDefault("userId", "anonymous"));

            auditRecorder.recordFailure(AuditEvent.builder()
                    .category(AuditCategory.OPERATION)
                    .eventType(AuditEventType.COMMAND_FAILED)
                    .actor(sessionActor(session))
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .failureReason(rootCause.getMessage())
                    .sourceType(AuditSourceType.WEBSOCKET)
                    .metadata(Map.of("exceptionType", rootCause.getClass().getSimpleName()))
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
