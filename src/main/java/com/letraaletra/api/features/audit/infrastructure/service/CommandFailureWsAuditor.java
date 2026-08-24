package com.letraaletra.api.features.audit.infrastructure.service;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.infrastructure.websocket.WsCommandFailureAuditor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommandFailureWsAuditor implements WsCommandFailureAuditor {
    private final BusinessAuditRecorder auditRecorder;

    @Override
    public void recordCommandFailure(WebSocketSession session, String gameId, Throwable rootCause) {
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
}
