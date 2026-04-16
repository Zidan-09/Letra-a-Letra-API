package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.LeftGameActorCommand;
import com.letraaletra.api.application.command.game.LeftGameCommand;
import com.letraaletra.api.application.output.actor.LeftGameResult;
import com.letraaletra.api.application.output.game.LeftGameOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.user.User;

import java.util.concurrent.CompletableFuture;

public class LeftGameUseCase {
    private final TokenService tokenService;
    private final ActorManager actorManager;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public LeftGameUseCase(TokenService tokenService, ActorManager actorManager, UserRepository userRepository, GameRepository gameRepository) {
        this.tokenService = tokenService;
        this.actorManager = actorManager;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public LeftGameOutput execute(LeftGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());
        Actor actor = actorManager.getOrCreate(gameId);

        CompletableFuture<LeftGameResult> future = actor.enqueueCommand(new LeftGameActorCommand(command.session()));

        LeftGameResult result = future.join();

        User user = userRepository.find(result.user());
        user.leaveGame();
        userRepository.save(user);

        if (result.isEmpty()) {
            actorManager.remove(result.game().getId());
        } else {
            gameRepository.save(result.game());
        }

        return buildReturn(result.game(), command.token(), result.gameOverResult());
    }

    private LeftGameOutput buildReturn(Game game, String token, GameOverResult gameOverResult) {
        return new LeftGameOutput(
                token,
                game,
                gameOverResult
        );
    }
}
