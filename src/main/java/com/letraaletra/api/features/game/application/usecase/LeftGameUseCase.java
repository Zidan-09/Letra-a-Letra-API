package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.application.command.actor.LeftGameActorCommand;
import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.application.output.actor.LeftGameResult;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.shared.infrastructure.concurrency.Actor;
import com.letraaletra.api.shared.infrastructure.concurrency.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.concurrent.CompletableFuture;

public class LeftGameUseCase implements UseCase<LeftGameInput, LeftGameOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> actorManager;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public LeftGameUseCase(TokenService tokenService, ActorManager<Game> actorManager, UserRepository userRepository, GameRepository gameRepository) {
        this.tokenService = tokenService;
        this.actorManager = actorManager;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public LeftGameOutput execute(LeftGameInput command) {
        String gameId = tokenService.getTokenContent(command.token());
        Actor actor = actorManager.get(gameId);

        CompletableFuture<LeftGameResult> future = actor.enqueueCommand(new LeftGameActorCommand(command.session()));

        LeftGameResult result = future.join();

        User user = userRepository.find(result.user()).orElse(null);
        validateUser(user);

        user.leaveGame();
        userRepository.save(user);

        if (result.isEmpty()) {
            actorManager.remove(result.game().getId());

            result.game().setGameStatus(GameStatus.CLOSED);

            gameRepository.save(result.game());
        }

        return buildReturn(result.game(), command.token(), result.gameOverResult());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private LeftGameOutput buildReturn(Game game, String token, GameOverResult gameOverResult) {
        return new LeftGameOutput(
                token,
                game,
                gameOverResult
        );
    }
}
