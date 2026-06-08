package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.application.input.ExpireTurnInput;
import com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult;
import com.letraaletra.api.features.game.application.output.ExpireTurnOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExpireTurnService implements UseCase<ExpireTurnInput, Optional<ExpireTurnOutput>> {
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;
    private final UserRepository userRepository;

    public ExpireTurnService(
            ActorManager<Game> gameActorManager,
            GameOverHandler gameOverHandler,
            UserRepository userRepository
    ) {
        this.gameActorManager = gameActorManager;
        this.gameOverHandler = gameOverHandler;
        this.userRepository = userRepository;
    }

    public Optional<ExpireTurnOutput> execute(ExpireTurnInput input) {
        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<Optional<ExpireTurnResult>> future = actor.enqueueCommand(
                new ExpireTurnActorCommand(input.version())
        );

        Optional<ExpireTurnResult> result = future.join();

        removeUserRoomId(result.orElse(null));

        return result.flatMap(this::buildOutput);
    }

    private void removeUserRoomId(ExpireTurnResult expireTurn) {
        if (expireTurn == null) return;

        if (expireTurn.gameOverResult().finished()) {
            gameOverHandler.handle(expireTurn.game(), expireTurn.gameOverResult());
        }

        if (expireTurn.removedBecauseAfk()) {
            handleAfkRemoval(expireTurn);
        }
    }

    private User getUserOrThrow(String userId) {
        return userRepository.find(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private void handleAfkRemoval(ExpireTurnResult expireTurn) {
        User user = getUserOrThrow(expireTurn.whoPassed());

        user.leaveGame();
        userRepository.save(user);
    }

    private Optional<ExpireTurnOutput> buildOutput(ExpireTurnResult result) {
        return Optional.of(
                new ExpireTurnOutput(
                        "TURN_EXPIRED",
                        result.whoPassed(),
                        result.game().getGameState().currentPlayerTurn(),
                        result.game(),
                        result.gameOverResult(),
                        result.removedBecauseAfk()
                )
        );
    }
}
