package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.DisconnectParticipantActorCommand;
import com.letraaletra.api.application.command.participant.DisconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.DisconnectParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DisconnectUseCase {
    private final ActorManager gameActorManager;
    private final DisconnectScheduler disconnectScheduler;
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;

    public DisconnectUseCase(
            ActorManager gameActorManager,
            DisconnectScheduler disconnectScheduler,
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        this.gameActorManager = gameActorManager;
        this.disconnectScheduler = disconnectScheduler;
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
    }

    public Optional<DisconnectParticipantOutput> execute(DisconnectParticipantCommand command) {
        String userId = command.user();
        if (userId == null) return Optional.empty();

        if (matchmakingRepository.onQueue(userId)) {
            matchmakingRepository.removeById(userId);
        }

        User user = userRepository.find(userId);
        if (user == null || !user.isInGame()) return Optional.empty();


        Actor actor = gameActorManager.getOrCreate(user.getCurrentGameId());

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

    private Optional<DisconnectParticipantOutput> buildReturn(Game game, String user) {
        return Optional.of(
                new DisconnectParticipantOutput(user, game)
        );
    }
}