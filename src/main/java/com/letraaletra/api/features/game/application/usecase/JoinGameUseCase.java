package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.application.command.actor.JoinGameActorCommand;
import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.shared.infrastructure.concurrency.Actor;
import com.letraaletra.api.shared.infrastructure.concurrency.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.concurrent.CompletableFuture;

public class JoinGameUseCase implements UseCase<JoinGameInput, JoinGameOutput> {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ActorManager<Game> actorManager;

    public JoinGameUseCase(UserRepository userRepository, TokenService tokenService, ActorManager<Game> actorManager) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.actorManager = actorManager;
    }

    public JoinGameOutput execute(JoinGameInput command) {
        String gameId = tokenService.getTokenContent(command.token());
        User user = userRepository.find(command.user()).orElse(null);
        validateUser(user);

        Actor actor = actorManager.get(gameId);
        CompletableFuture<Game> future = actor.enqueueCommand(new JoinGameActorCommand(user, command.session()));

        Game game = future.join();
        userRepository.save(user);

        return buildReturn(command.token(), game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private JoinGameOutput buildReturn(String token, Game game) {
        return new JoinGameOutput(token, game);
    }
}
