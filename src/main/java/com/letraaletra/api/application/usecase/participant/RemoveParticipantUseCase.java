package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.RemoveParticipantActorCommand;
import com.letraaletra.api.application.command.participant.RemoveParticipantCommand;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorRepository;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

public class RemoveParticipantUseCase {
    private final ActorRepository gameActorRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public RemoveParticipantUseCase(ActorRepository gameActorRepository, GameRepository gameRepository, UserRepository userRepository) {
        this.gameActorRepository = gameActorRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public void execute(RemoveParticipantCommand command) {
        Actor actor = gameActorRepository.getOrCreate(command.gameId());

        CompletableFuture<Void> future = actor.enqueueCommand(new RemoveParticipantActorCommand(
                userRepository, gameRepository, command.userId()
        ));

        future.join();
    }
}
