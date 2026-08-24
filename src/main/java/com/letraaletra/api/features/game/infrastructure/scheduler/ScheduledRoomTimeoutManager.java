package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.application.port.CloseRoomService;
import com.letraaletra.api.features.game.domain.room.CloseRoomResult;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.room.RoomClosed;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ScheduledRoomTimeoutManager implements RoomTimeoutManager {
    private static final String SOURCE_DETAIL = "ROOM_TIMEOUT_SCHEDULER";

    private final CloseRoomService closeRoomService;
    private final GameNotifier gameNotifier;
    private final AuditService auditService;
    private final BusinessAuditRecorder auditRecorder;
    private final OperationContext operationContext;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Map<UUID, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

    public void start(Game game) {
        cancel(game);

        ScheduledFuture<?> future = scheduler.schedule(() -> handleTimeout(game), 5, TimeUnit.MINUTES);

        timers.put(game.getId(), future);
    }

    public void cancel(Game game) {
        ScheduledFuture<?> future = timers.remove(game.getId());

        if (future != null) {
            future.cancel(false);
        }
    }

    private void handleTimeout(Game game) {
        timers.remove(game.getId());

        operationContext.runAsOperation(UUID.randomUUID(), game.getId().toString(), () -> {
            CloseRoomResult result = closeRoomService.close(game);

            RoomClosed data = new RoomClosed(result.event(), result.reason());

            auditService.game(
                    game.getId().toString(),
                    null,
                    Level.INFO,
                    "A sala foi fechada por inatividade"
            );

            auditRecorder.record(AuditEvent.builder()
                    .category(AuditCategory.GAME)
                    .eventType(AuditEventType.ROOM_CLOSED_INACTIVITY)
                    .actor(AuditActor.system())
                    .resourceType(AuditResourceType.ROOM)
                    .resourceId(game.getId().toString())
                    .correlationId(game.getId().toString())
                    .sourceType(AuditSourceType.SCHEDULER)
                    .sourceDetail(SOURCE_DETAIL)
                    .build());

            gameNotifier.notifierAll(result.game(), data);
        });
    }
}
