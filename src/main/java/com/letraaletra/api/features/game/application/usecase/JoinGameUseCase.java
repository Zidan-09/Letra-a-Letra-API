package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.JoinGameActorCommand;
import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JoinGameUseCase implements UseCase<JoinGameInput, JoinGameOutput> {
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;

    public JoinGameUseCase(UserRepository userRepository, ActorManager<Game> actorManager) {
        this.userRepository = userRepository;
        this.actorManager = actorManager;
    }

    public JoinGameOutput execute(JoinGameInput input) {
        UUID gameId = input.gameId();

        User user = userRepository.find(input.user())
                .orElseThrow(UserNotFoundException::new);

        validateUser(user);

        Actor actor = actorManager.get(gameId);
        CompletableFuture<Game> future = actor.enqueueCommand(new JoinGameActorCommand(user, input.session()));

        Game game = future.join();
        userRepository.save(user);

        return buildOutput(game);
    }

    private void validateUser(User user) {
        if (!user.isNotInGame()) {
            throw new UserAlreadyInGameException();
        }
    }

    private JoinGameOutput buildOutput(Game game) {
        return new JoinGameOutput(game);
    }
}
