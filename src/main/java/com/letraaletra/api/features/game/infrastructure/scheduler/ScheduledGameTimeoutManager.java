package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.application.input.CloseRoomInput;
import com.letraaletra.api.features.game.application.output.CloseRoomOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.application.service.CloseRoomDueToTimeoutService;
import com.letraaletra.api.features.game.domain.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ScheduledGameTimeoutManager implements GameTimeoutManager {
    @Autowired
    private CloseRoomDueToTimeoutService closeRoomDueToTimeoutService;

    @Autowired
    private GameNotifier gameNotifier;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Map<String, ScheduledFuture<?>> timers = new ConcurrentHashMap<>();

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

        gameNotifier.notifierAll(output.game(), data);
    }
}
