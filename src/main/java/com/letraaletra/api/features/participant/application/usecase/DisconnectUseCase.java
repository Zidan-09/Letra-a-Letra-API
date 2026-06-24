package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.DisconnectParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.DisconnectScheduler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DisconnectUseCase implements UseCase<DisconnectParticipantInput, Optional<DisconnectParticipantOutput>> {
    private final ActorManager<Game> gameActorManager;
    private final DisconnectScheduler disconnectScheduler;
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;

    public DisconnectUseCase(
            ActorManager<Game> gameActorManager,
            DisconnectScheduler disconnectScheduler,
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        this.gameActorManager = gameActorManager;
        this.disconnectScheduler = disconnectScheduler;
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
    }

    public Optional<DisconnectParticipantOutput> execute(DisconnectParticipantInput input) {
        UUID userId = input.user();
        if (userId == null) return Optional.empty();

        if (matchmakingRepository.onQueue(userId)) {
            matchmakingRepository.removeById(userId);
        }

        User user = userRepository.find(userId).orElse(null);
        if (user == null || user.isNotInGame()) return Optional.empty();


        Actor actor = gameActorManager.get(user.getCurrentGameId());

        CompletableFuture<Optional<Game>> future = actor.enqueueCommand(
                new DisconnectParticipantActorCommand(userId, disconnectScheduler)
        );

        Optional<Game> gameOpt = future.join();

        if (gameOpt.isEmpty()) {
            user.leaveGame();
            userRepository.save(user);
            return Optional.empty();
        }

        return buildReturn(gameOpt.get(), userId);
    }

    private Optional<DisconnectParticipantOutput> buildReturn(Game game, UUID user) {
        return Optional.of(
                new DisconnectParticipantOutput(user, game)
        );
    }
}