package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.LeftGameActorCommand;
import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.domain.actor.result.LeftGameResult;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public class LeftGameUseCase implements UseCase<LeftGameInput, LeftGameOutput> {
    private final ActorManager<Game> actorManager;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final RoomTimeoutManager roomTimeoutManager;
    private final GameOverService gameOverService;

    public LeftGameUseCase(
            ActorManager<Game> actorManager,
            UserRepository userRepository,
            GameRepository gameRepository,
            RoomTimeoutManager roomTimeoutManager,
            GameOverService gameOverService
    ) {
        this.actorManager = actorManager;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.roomTimeoutManager = roomTimeoutManager;
        this.gameOverService = gameOverService;
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

        if (result.game().getGameStatus().equals(GameStatus.WAITING)) {
            roomTimeoutManager.start(result.game());

        } else if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(result.game().getId());

        }

        HandledGameOver handledGameOver = result.gameOver()
                .map(over -> gameOverService.handle(result.game(), over))
                .orElseGet(HandledGameOver::withoutRanking);

        userRepository.save(user);
        gameRepository.save(result.game());

        return new LeftGameOutput(result.game(), result.gameOver(), handledGameOver);
    }
}
