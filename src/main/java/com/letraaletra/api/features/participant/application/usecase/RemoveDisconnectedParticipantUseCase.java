package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.RemoveDisconnectedParticipantActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.RemoveParticipantResult;
import com.letraaletra.api.features.participant.application.input.RemoveDisconnectedParticipantInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.concurrent.CompletableFuture;

public class RemoveDisconnectedParticipantUseCase implements UseCase<RemoveDisconnectedParticipantInput, Void> {
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;
    private final GameOverFinalizer gameOverFinalizer;

    public RemoveDisconnectedParticipantUseCase(
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameOverFinalizer gameOverFinalizer
    ) {
        this.userRepository = userRepository;
        this.actorManager = actorManager;
        this.gameOverFinalizer = gameOverFinalizer;
    }

    @Override
    public Void execute(RemoveDisconnectedParticipantInput input) {
        Actor actor = actorManager.get(input.gameId());

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        CompletableFuture<RemoveParticipantResult> future = actor.enqueueCommand(new RemoveDisconnectedParticipantActorCommand(
                user
        ));

        RemoveParticipantResult result = future.join();

        if (result.gameOver().isPresent()) {
            gameOverFinalizer.finish(result.game(), result.gameOver().get());

            return null;
        }

        if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(result.game().getId());
        }

        userRepository.save(user);

        return null;
    }
}
