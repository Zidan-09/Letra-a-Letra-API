package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.application.input.CloseRoomInput;
import com.letraaletra.api.features.game.application.output.CloseRoomOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.application.service.CloseRoomDueToTimeoutService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.application.port.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class ScheduledGameTimeoutManager implements GameTimeoutManager {
    private final CloseRoomDueToTimeoutService closeRoomDueToTimeoutService;
    private final GameNotifier gameNotifier;
    private final AuditService auditService;

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

        CloseRoomOutput output = closeRoomDueToTimeoutService.execute(
                new CloseRoomInput(game)
        );

        RoomClosed data = new RoomClosed(output.event(), output.reason());

        auditService.game(
                game.getId().toString(),
                null,
                Level.INFO,
                "A sala foi fechada por inatividade"
        );

        gameNotifier.notifierAll(output.game(), data);
    }
}
