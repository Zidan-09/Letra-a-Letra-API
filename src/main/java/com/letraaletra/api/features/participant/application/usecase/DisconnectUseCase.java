package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.DisconnectParticipantActorCommand;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.participant.port.DisconnectScheduler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DisconnectUseCase implements UseCase<DisconnectParticipantInput, Optional<DisconnectParticipantOutput>> {
    private final ActorManager<Game> gameActorManager;
    private final DisconnectScheduler disconnectScheduler;
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

    public DisconnectUseCase(
            ActorManager<Game> gameActorManager,
            DisconnectScheduler disconnectScheduler,
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        this.gameActorManager = gameActorManager;
        this.disconnectScheduler = disconnectScheduler;
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<DisconnectParticipantOutput> execute(DisconnectParticipantInput input) {
        UUID userId = input.user();
        if (userId == null) return Optional.empty();

        if (queueRepository.onQueue(userId)) {
            queueRepository.remove(userId);
        }

        User user = userRepository.find(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.isNotInGame()) return Optional.empty();

        try {
            Actor actor = gameActorManager.get(user.getCurrentGameId());

            CompletableFuture<Optional<Game>> future = actor.enqueueCommand(
                    new DisconnectParticipantActorCommand(userId)
            );

            Optional<Game> game = future.join();

            if (game.isEmpty()) {
                user.leaveGame();
                userRepository.save(user);
                return Optional.empty();
            } else {
                disconnectScheduler.start(userId, game.get().getId());
            }

            return Optional.of(new DisconnectParticipantOutput(userId, game.get()));

        } catch (GameNotFoundException e) {
            return Optional.empty();
        }
    }
}