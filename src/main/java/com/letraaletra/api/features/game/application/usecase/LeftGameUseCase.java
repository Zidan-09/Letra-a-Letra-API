package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.domain.actor.result.LeftGameResult;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public class LeftGameUseCase implements UseCase<LeftGameInput, LeftGameOutput> {
    private final ActorManager<Game> actorManager;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameTimeoutManager gameTimeoutManager;
    private final GameOverService gameOverService;

    public LeftGameUseCase(
            ActorManager<Game> actorManager,
            UserRepository userRepository,
            GameRepository gameRepository,
            GameTimeoutManager gameTimeoutManager,
            GameOverService gameOverService
    ) {
        this.actorManager = actorManager;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.gameOverService = gameOverService;
    }

    @Override
    public LeftGameOutput execute(LeftGameInput input) {
        Actor actor = actorManager.get(input.gameId());

        CompletableFuture<LeftGameResult> future = actor.enqueueCommand(new LeftGameActorCommand(
                userRepository,
                input.session()
        ));

        LeftGameResult result = future.join();

        if (result.game().getGameStatus().equals(GameStatus.WAITING)) {
            gameTimeoutManager.start(result.game());

        } else if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(result.game().getId());

        }

        result.gameOver().ifPresent(over -> gameOverService.handle(result.game(), over));

        gameRepository.save(result.game());

        return new LeftGameOutput(result.game(), result.gameOver());
    }
}
