package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.application.command.actor.RemoveParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.RemoveParticipantInput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCaseWithoutOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.repository.game.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RemoveParticipantUseCase implements UseCaseWithoutOutput<RemoveParticipantInput> {
    private final ActorManager<Game> gameActorManager;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public RemoveParticipantUseCase(ActorManager<Game> gameActorManager, GameRepository gameRepository, UserRepository userRepository) {
        this.gameActorManager = gameActorManager;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public void execute(RemoveParticipantInput command) {
        Actor actor = gameActorManager.get(command.gameId());

        CompletableFuture<Optional<Game>> future = actor.enqueueCommand(new RemoveParticipantActorCommand(
                userRepository, command.userId()
        ));

        Optional<Game> game = future.join();

        game.ifPresent(g -> {
            if (g.getParticipants().isEmpty()) {
                g.setGameStatus(GameStatus.CLOSED);

                gameRepository.save(g);
            }
        });
    }
}
