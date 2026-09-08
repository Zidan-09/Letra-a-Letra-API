package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.actor.command.ReconnectParticipantActorCommand;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.game.domain.participant.port.DisconnectScheduler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReconnectUseCase implements UseCase<ReconnectParticipantInput, Optional<ReconnectParticipantOutput>> {
    private final ActorManager<Game> actorManager;
    private final DisconnectScheduler disconnectScheduler;
    private final UserRepository userRepository;

    public ReconnectUseCase(
            ActorManager<Game> actorManager,
            DisconnectScheduler disconnectScheduler,
            UserRepository userRepository
    ) {
        this.actorManager = actorManager;
        this.disconnectScheduler = disconnectScheduler;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ReconnectParticipantOutput> execute(ReconnectParticipantInput input) {
        UUID userId = input.user();
        if (userId == null) return Optional.empty();

        User user = userRepository.find(userId).orElse(null);
        if (user == null) return Optional.empty();

        if (user.isNotInGame()) {
            return Optional.empty();
        }

        try {
            Actor actor = actorManager.get(user.getCurrentGameId());

            CompletableFuture<Optional<Game>> future = actor.enqueueCommand(
                    new ReconnectParticipantActorCommand(userId, input.session())
            );

            Optional<Game> game = future.join();

            if (game.isEmpty()) {
                return Optional.empty();
            }

            disconnectScheduler.cancel(userId, game.get().getId());

            return buildReturn(game.get());

        } catch (GameNotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<ReconnectParticipantOutput> buildReturn(Game game) {
        return Optional.of(new ReconnectParticipantOutput(
                game
        ));
    }
}
