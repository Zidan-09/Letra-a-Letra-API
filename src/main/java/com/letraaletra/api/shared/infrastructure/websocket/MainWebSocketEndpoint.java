package com.letraaletra.api.shared.infrastructure.websocket;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.infrastructure.listener.ShutdownListener;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorWsResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
public class MainWebSocketEndpoint extends TextWebSocketHandler {
    private final WsConnectionRegistry connectionRegistry;
    private final WsRequestRouter requestRouter;
    private final List<WsLifecycleListener> lifecycleListeners;
    private final WsUserDirectory userDirectory;
    private final WsMessageSender messageSender;
    private final JsonMapper jsonMapper;
    private final Validator validator;
    private final ShutdownListener shutdownListener;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        connectionRegistry.save(session);

        for (WsLifecycleListener listener : lifecycleListeners) {
            listener.onConnected(session);
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        String userId = (String) session.getAttributes().get("userId");

        boolean knownUser = userDirectory.exists(UUID.fromString(userId));

        WsRequest request;

        try {
            request = jsonMapper.readValue(
                    message.getPayload(),
                    WsRequest.class
            );

            Set<ConstraintViolation<WsRequest>> violations = validator.validate(request);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            requestRouter.dispatch(request, session);

        } catch (Exception e) {
            sendError(e, userId, knownUser);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {

        if (shutdownListener.isShuttingDown()) {
            return;
        }

        connectionRegistry.remove(session);

        for (WsLifecycleListener listener : lifecycleListeners) {
            listener.onDisconnected(session);
        }
    }

    private void sendError(Exception ex, String userId, boolean knownUser) {

        Throwable cause = ex;

        if (ex instanceof CompletionException && ex.getCause() != null) {
            cause = ex.getCause();
        }

        String message;

        if (cause instanceof DomainException appEx) {
            message = appEx.getMessage();
        } else {
            message = "an unexpected internal server error occurred";
        }

        ErrorWsResponse json = new ErrorWsResponse(message);

        if (knownUser) {
            messageSender.sendToUser(UUID.fromString(userId), json);
        }
    }
}
