package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.port.ExpireTurnService;
import com.letraaletra.api.features.game.application.service.GameOverHandler;
import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.domain.ExpireTurnResult;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExpireTurnTimeoutService implements ExpireTurnService {
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;

    @Override
    public Optional<ExpireTurnResult> expire(UUID gameId, int version) {
        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Optional<com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult>> future = actor.enqueueCommand(
                new ExpireTurnActorCommand(version)
        );

        Optional<com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult> result = future.join();

        result.ifPresent(r ->
                r.gameOver().ifPresent(gameOver ->
                        gameOverHandler.handle(r.game(), gameOver)
                )
        );

        return result.flatMap(this::buildOutput);
    }

    private Optional<ExpireTurnResult> buildOutput(com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult result) {
        return Optional.of(
                new ExpireTurnResult(
                        "TURN_EXPIRED",
                        result.whoPassed(),
                        result.game().getGameState().currentPlayerTurn(),
                        result.game(),
                        result.gameOver()
                )
        );
    }
}
