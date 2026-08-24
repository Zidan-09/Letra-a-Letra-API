package com.letraaletra.api.features.participant.infrastructure.websocket;

import com.letraaletra.api.features.participant.application.input.RemoveDisconnectedParticipantInput;
import com.letraaletra.api.features.game.domain.participant.port.DisconnectScheduler;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import com.letraaletra.api.shared.application.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class DisconnectManager implements DisconnectScheduler {
    private final UseCase<RemoveDisconnectedParticipantInput, Void> useCase;
    private final BusinessAuditRecorder auditRecorder;
    private final OperationContext operationContext;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    @Override
    public void start(UUID userId, UUID gameId) {
        String key = buildKey(userId, gameId);

        cancel(userId, gameId);

        ScheduledFuture<?> future = scheduler.schedule(() -> handleTimeout(userId, gameId), 60, TimeUnit.SECONDS);

        timers.put(key, future);
    }

    @Override
    public void cancel(UUID userId, UUID gameId) {
        String key = buildKey(userId, gameId);

        ScheduledFuture<?> future = timers.remove(key);

        if (future != null) {
            future.cancel(false);
        }
    }

    private void handleTimeout(UUID userId, UUID gameId) {
        String key = buildKey(userId, gameId);
        timers.remove(key);

        operationContext.runAsOperation(UUID.randomUUID(), gameId.toString(), () -> {
            useCase.execute(new RemoveDisconnectedParticipantInput(
                    gameId, userId
            ));

            auditRecorder.record(AuditEvent.builder()
                    .category(AuditCategory.GAME)
                    .eventType(AuditEventType.PLAYER_REMOVED_INACTIVITY)
                    .actor(AuditActor.system())
                    .targetUserId(userId)
                    .resourceType(AuditResourceType.USER)
                    .resourceId(userId.toString())
                    .correlationId(gameId.toString())
                    .sourceType(AuditSourceType.SCHEDULER)
                    .sourceDetail("DISCONNECT_TIMEOUT_SCHEDULER")
                    .build());
        });
    }

    private String buildKey(UUID userId, UUID gameId) {
        return userId.toString() + ":" + gameId.toString();
    }
}