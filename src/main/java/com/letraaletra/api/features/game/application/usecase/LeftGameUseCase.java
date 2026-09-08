package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.domain.actor.result.LeftGameResult;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public class LeftGameUseCase implements UseCase<LeftGameInput, LeftGameOutput> {
    private final ActorManager<Game> actorManager;
    private final UserRepository userRepository;
    private final GameOverFinalizer gameOverFinalizer;

    public LeftGameUseCase(
            ActorManager<Game> actorManager,
            UserRepository userRepository,
            GameOverFinalizer gameOverFinalizer
    ) {
        this.actorManager = actorManager;
        this.userRepository = userRepository;
        this.gameOverFinalizer = gameOverFinalizer;
    }

    @Override
    public LeftGameOutput execute(LeftGameInput input) {
        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        Actor actor = actorManager.get(input.gameId());

        CompletableFuture<LeftGameResult> future = actor.enqueueCommand(new LeftGameActorCommand(
                user,
                input.session()
        ));

        LeftGameResult result = future.join();

        if (result.gameOver().isPresent()) {
            HandledGameOver handledGameOver = gameOverFinalizer.finish(result.game(), result.gameOver().get());

            return new LeftGameOutput(result.game(), result.gameOver(), handledGameOver);
        }

        if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(result.game().getId());
        }

        userRepository.save(user);

        return new LeftGameOutput(result.game(), result.gameOver(), HandledGameOver.withoutRanking());
    }
}
