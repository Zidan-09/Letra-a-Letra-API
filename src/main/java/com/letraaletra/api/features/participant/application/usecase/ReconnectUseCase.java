package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.shared.infrastructure.concurrency.Actor;
import com.letraaletra.api.shared.infrastructure.concurrency.ActorManager;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;

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

    public Optional<ReconnectParticipantOutput> execute(ReconnectParticipantInput command) {
        String userId = command.user();
        if (userId == null) return Optional.empty();

        User user = userRepository.find(userId).orElse(null);
        if (user == null) return Optional.empty();

        if (user.isNotInGame()) {
            return Optional.empty();
        }

        try {
            Actor actor = actorManager.get(user.getCurrentGameId());

            Game game = actor.getGame();

            Participant participant = game.getParticipantByUserId(userId);
            if (participant == null) return Optional.empty();

            disconnectScheduler.cancel(userId, game.getId());

            game.reconnect(userId, command.session());

            return buildReturn(game);

        } catch (Exception e) {
            user.leaveGame();
            userRepository.save(user);
            return Optional.empty();
        }
    }

    private Optional<ReconnectParticipantOutput> buildReturn(Game game) {
        return Optional.of(new ReconnectParticipantOutput(
                game
        ));
    }
}
