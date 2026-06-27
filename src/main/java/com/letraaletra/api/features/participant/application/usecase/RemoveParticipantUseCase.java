package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.RemoveParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.RemoveParticipantInput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RemoveParticipantUseCase implements UseCase<RemoveParticipantInput, Void> {
    private final ActorManager<Game> gameActorManager;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public RemoveParticipantUseCase(ActorManager<Game> gameActorManager, GameRepository gameRepository, UserRepository userRepository) {
        this.gameActorManager = gameActorManager;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public Void execute(RemoveParticipantInput input) {
        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<Optional<Game>> future = actor.enqueueCommand(new RemoveParticipantActorCommand(
                userRepository, input.userId()
        ));

        Optional<Game> game = future.join();

        game.ifPresent(g -> {
            if (g.getParticipants().isEmpty()) {
                g.setGameStatus(GameStatus.CLOSED);

                gameRepository.save(g);
            }
        });

        return null;
    }
}
