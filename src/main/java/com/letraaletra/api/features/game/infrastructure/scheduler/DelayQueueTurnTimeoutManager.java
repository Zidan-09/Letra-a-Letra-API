package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.application.input.ExpireTurnInput;
import com.letraaletra.api.features.game.application.output.ExpireTurnOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.service.ExpireTurnService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.DelayQueue;

@Service
public class DelayQueueTurnTimeoutManager implements TurnTimeoutManager {
    private final ExpireTurnService expireTurnService;

    private final DelayQueue<GameTurn> queue = new DelayQueue<>();

    private final GameNotifier gameNotifier;

    private final Logger logger = LoggerFactory.getLogger(DelayQueueTurnTimeoutManager.class);

    public DelayQueueTurnTimeoutManager(ExpireTurnService expireTurnService, GameNotifier gameNotifier) {
        this.expireTurnService = expireTurnService;
        this.gameNotifier = gameNotifier;
        startScheduler();
    }

    @Override
    public void start(Game game) {
        queue.put(new GameTurn(
                game.getId(),
                game.getGameState().getCurrentTurnEnds(),
                game.getGameState().getVersion()
        ));
    }

    private void startScheduler() {
        Thread thread = new Thread(this::processLoop);
        thread.setDaemon(true);
        thread.start();
    }

    private void processLoop() {
        while (true) {
            try {
                GameTurn next = queue.take();
                handleTurnTimeout(next);

            } catch (Exception e) {
                if (!e.getMessage().equals("game_not_found")) {
                    logger.warn("Error on process end of turn {}-{}", e.getMessage(), e.getStackTrace());
                }
            }
        }
    }

    private void handleTurnTimeout(GameTurn gameTurn) {
        ExpireTurnInput command = new ExpireTurnInput(gameTurn.gameId(), gameTurn.version());

        Optional<ExpireTurnOutput> output = expireTurnService.execute(command);

        if (output.isEmpty()) return;

        ExpireTurnOutput result = output.get();

        TurnExpired data = new TurnExpired(
                result.event(),
                new TurnExpired.ExpiredData(result.user().toString(), result.currentPlayerTurnId().toString())
        );

        gameNotifier.notifierAll(result.game(), data);

        if (result.removedBecauseAfk()) {
            gameNotifier.notifierOne(
                    result.user(),
                    new RemovedBecauseInactivity("REMOVED_BECAUSE_INACTIVITY")
            );
        }

        if (result.gameOverResult().finished()) {
            gameNotifier.notifierGameOver(result.game(), result.gameOverResult());
        }

        if (result.game().getGameStatus().equals(GameStatus.RUNNING)) {
            start(result.game());
        }
    }
}
