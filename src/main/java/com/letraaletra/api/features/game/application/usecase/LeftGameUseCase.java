package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.domain.actor.output.LeftGameResult;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LeftGameUseCase implements UseCase<LeftGameInput, LeftGameOutput> {
    private final ActorManager<Game> actorManager;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public LeftGameUseCase(ActorManager<Game> actorManager, UserRepository userRepository, GameRepository gameRepository) {
        this.actorManager = actorManager;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public LeftGameOutput execute(LeftGameInput input) {
        UUID gameId = input.gameId();
        Actor actor = actorManager.get(gameId);

        CompletableFuture<LeftGameResult> future = actor.enqueueCommand(new LeftGameActorCommand(input.session()));

        LeftGameResult result = future.join();

        User user = userRepository.find(result.user())
                .orElseThrow(UserNotFoundException::new);

        user.leaveGame();
        userRepository.save(user);

        if (result.isEmpty()) {
            actorManager.remove(result.game().getId());

            result.game().setGameStatus(GameStatus.CLOSED);

            gameRepository.save(result.game());
        }

        return buildOutput(result);
    }

    private LeftGameOutput buildOutput(LeftGameResult result) {
        return new LeftGameOutput(
                result.game(),
                result.gameOver()
        );
    }
}
