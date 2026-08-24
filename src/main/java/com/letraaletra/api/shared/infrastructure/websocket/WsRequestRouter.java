package com.letraaletra.api.shared.infrastructure.websocket;

import com.letraaletra.api.shared.application.port.AuditService;
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
public class WsRequestRouter {

    private final Map<Class<? extends WsRequest>, RoomRequestHandler<?>> handlers;
    private final WsUserDirectory userDirectory;
    private final AuditService auditService;
    private final WsCommandFailureAuditor commandFailureAuditor;
    private final MdcOperationContext operationContext;

    public WsRequestRouter(
            List<RoomRequestHandler<?>> handlerList,
            WsUserDirectory userDirectory,
            AuditService auditService,
            WsCommandFailureAuditor commandFailureAuditor,
            MdcOperationContext operationContext
    ) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(RoomRequestHandler::getType, Function.identity()));
        this.userDirectory = userDirectory;
        this.auditService = auditService;
        this.commandFailureAuditor = commandFailureAuditor;
        this.operationContext = operationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends WsRequest> void dispatch(T request, WebSocketSession session) {
        RoomRequestHandler<T> handler = (RoomRequestHandler<T>) handlers.get(request.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("Handler not found for: " + request.getClass());
        }

        boolean suppressAudit = request.suppressCommandAudit();
        String requestId = UUID.randomUUID().toString();
        String actionName = formatActionName(request.getClass().getSimpleName());
        String gameId = extractGameId(request);
        String userDisplay = getUserDisplay(session);

        operationContext.bindRequest(requestId, "WS " + actionName);

        try {
            handler.handle(request, session);

            if (!suppressAudit) {
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
            if (!suppressAudit) {
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

                commandFailureAuditor.recordCommandFailure(session, gameId, result);
            }

            throw ex;
        } finally {
            operationContext.unbindRequest();
        }
    }

    private String getUserDisplay(WebSocketSession session) {
        String userIdStr = (String) session.getAttributes().get("userId");
        if (userIdStr == null) return "anonymous";

        try {
            UUID userId = UUID.fromString(userIdStr);
            return userDirectory.describe(userId).orElse(userIdStr);
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
