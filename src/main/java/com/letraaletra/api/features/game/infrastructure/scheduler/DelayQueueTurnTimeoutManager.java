package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.application.input.ExpireTurnInput;
import com.letraaletra.api.features.game.application.output.ExpireTurnOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.service.ExpireTurnService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.application.port.GameResponseAssembler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.DelayQueue;

@Service
public class DelayQueueTurnTimeoutManager implements TurnTimeoutManager {
    private final ExpireTurnService expireTurnService;
    private final GameResponseAssembler gameResponseAssembler;

    private final DelayQueue<GameTurn> queue = new DelayQueue<>();

    private final GameNotifier gameNotifier;

    private final AuditService auditService;

    private final Logger logger = LoggerFactory.getLogger(DelayQueueTurnTimeoutManager.class);

    public DelayQueueTurnTimeoutManager(
            ExpireTurnService expireTurnService,
            GameResponseAssembler gameResponseAssembler,
            GameNotifier gameNotifier,
            AuditService auditService
    ) {
        this.expireTurnService = expireTurnService;
        this.gameResponseAssembler = gameResponseAssembler;
        this.gameNotifier = gameNotifier;
        this.auditService = auditService;
        startScheduler();
    }

    @Override
    public void start(Game game) {
        queue.put(new GameTurn(
                game.getId(),
                game.getGameState().getMatchId(),
                getCurrentPlayer(game.getGameState()),
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
                    logger.error(
                            "Error on process end of turn {}-{}",
                            e.getMessage(),
                            e.getStackTrace()
                    );
                }
            }
        }
    }

    private void handleTurnTimeout(GameTurn gameTurn) {
        ExpireTurnInput command = new ExpireTurnInput(gameTurn.gameId(), gameTurn.version());

        Optional<ExpireTurnOutput> output = expireTurnService.execute(command);

        if (output.isEmpty()) return;

        auditService.game(
                gameTurn.gameId().toString(),
                gameTurn.matchId().toString(),
                Level.INFO,
                "Jogador %s (%s) passou a vez (%s/3) para ser removido".formatted(
                        gameTurn.player().getNickname(),
                        gameTurn.player().getUserId(),
                        gameTurn.player().getPassedTurn()
                )
        );

        ExpireTurnOutput result = output.get();

        TurnExpired data = new TurnExpired(
                result.event(),
                new TurnExpired.ExpiredData(result.user(), result.currentPlayerTurnId())
        );

        gameNotifier.notifierAll(result.game(), data);

        result.gameOver().ifPresent(over -> {
            gameNotifier.notifierOne(
                    result.user(),
                    new RemovedBecauseInactivity("REMOVED_BECAUSE_INACTIVITY")
            );

            WsResponse dto = gameResponseAssembler.assembleGameOver(result.game(), over);

            gameNotifier.notifierGameOver(result.game(), dto);
        });

        if (result.game().getGameStatus().equals(GameStatus.RUNNING)) {
            start(result.game());
        }
    }

    private Player getCurrentPlayer(GameState state) {
        return state.getPlayerOrThrow(state.currentPlayerTurn());
    }
}
