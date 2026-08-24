package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.application.port.ExpireTurnService;
import com.letraaletra.api.features.game.application.port.TransactionalExecutorService;
import com.letraaletra.api.features.game.domain.*;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.game.domain.room.RemovedBecauseInactivity;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.application.output.ExpireTurnTimeoutResult;
import com.letraaletra.api.features.game.domain.turn.GameTurn;
import com.letraaletra.api.features.game.domain.turn.TurnExpired;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import com.letraaletra.api.features.game.infrastructure.websocket.assembler.GameResponseAssembler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.DelayQueue;

@Service
@RequiredArgsConstructor
public class DelayQueueTurnTimeoutManager implements TurnTimeoutManager {
    private final ExpireTurnService expireTurnService;
    private final GameResponseAssembler gameResponseAssembler;
    private final RoomTimeoutManager roomTimeoutManager;
    private final TransactionalExecutorService transactionExecutor;

    private final DelayQueue<GameTurn> queue = new DelayQueue<>();

    private final GameNotifier gameNotifier;

    private final AuditService auditService;
    private final BusinessAuditRecorder auditRecorder;
    private final OperationContext operationContext;

    private final Logger logger = LoggerFactory.getLogger(DelayQueueTurnTimeoutManager.class);

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

    @Scheduled(fixedDelay = 10)
    private void processLoop() {
        GameTurn next;

        while ((next = queue.poll()) != null) {
            try {
                handleTurnTimeout(next);
            } catch (Exception e) {
                if (!(e instanceof GameNotFoundException)) {
                    logger.error("Error on process end of turn: {}", e.getMessage(), e);
                }
            }
        }
    }

    private void handleTurnTimeout(GameTurn gameTurn) {
        operationContext.runAsOperation(
                UUID.randomUUID(),
                gameTurn.gameId().toString(),
                () -> processTurnTimeout(gameTurn)
        );
    }

    private void processTurnTimeout(GameTurn gameTurn) {
        Optional<ExpireTurnTimeoutResult> optResult =
                transactionExecutor.execute(() ->
                        expireTurnService.expire(
                                gameTurn.gameId(),
                                gameTurn.version()
                        )
                );

        if (optResult.isEmpty()) return;

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

        if (gameTurn.player().getPassedTurn() == 3) {
            auditService.game(
                    gameTurn.gameId().toString(),
                    gameTurn.matchId().toString(),
                    Level.INFO,
                    "Jogador %s (%s) foi removido da sala por inatividade".formatted(
                            gameTurn.player().getNickname(),
                            gameTurn.player().getUserId()
                    )
            );

            auditRecorder.record(AuditEvent.builder()
                    .category(AuditCategory.GAME)
                    .eventType(AuditEventType.PLAYER_REMOVED_INACTIVITY)
                    .actor(AuditActor.system())
                    .targetUserId(gameTurn.player().getUserId())
                    .resourceType(AuditResourceType.USER)
                    .resourceId(gameTurn.player().getUserId().toString())
                    .correlationId(gameTurn.gameId().toString())
                    .sourceType(AuditSourceType.SCHEDULER)
                    .sourceDetail("TURN_TIMEOUT_SCHEDULER")
                    .metadata(Map.of("matchId", gameTurn.matchId().toString()))
                    .build());
        }

        ExpireTurnTimeoutResult result = optResult.get();

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

            WsResponse dto = gameResponseAssembler.assembleGameOver(result.game(), over, result.handledGameOver());

            Game game = result.game();

            auditService.game(
                    game.getId().toString(),
                    game.getGameState().getMatchId().toString(),
                    Level.INFO,
                    "A partida acabou | Vencedor: {} ({}) - Pontuação: {} | Perdedor: {} ({}) - Pontuação: {}",
                    over.winner().getNickname(),
                    over.winner().getUserId().toString(),
                    over.winner().getScore(),
                    over.loser().getNickname(),
                    over.loser().getUserId().toString(),
                    over.loser().getScore()
            );

            gameNotifier.notifierGameOver(result.game(), dto);

            roomTimeoutManager.start(game);
        });

        if (result.game().getGameStatus().equals(GameStatus.RUNNING)) {
            start(result.game());
        }
    }

    private Player getCurrentPlayer(GameState state) {
        return state.getPlayerOrThrow(state.currentPlayerTurn());
    }
}
