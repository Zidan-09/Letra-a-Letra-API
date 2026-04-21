package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.RemoveParticipantActorCommand;
import com.letraaletra.api.application.command.participant.RemoveParticipantCommand;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

public class RemoveParticipantUseCase {
    private final ActorManager gameActorManager;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public RemoveParticipantUseCase(ActorManager gameActorManager, GameRepository gameRepository, UserRepository userRepository) {
        this.gameActorManager = gameActorManager;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public void execute(RemoveParticipantCommand command) {
        Actor actor = gameActorManager.get(command.gameId());

        CompletableFuture<Void> future = actor.enqueueCommand(new RemoveParticipantActorCommand(
                userRepository, gameRepository, gameActorManager, command.userId()
        ));

        future.join();
    }
}
