package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.DisconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.DisconnectParticipantOutput;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.Optional;

public class DisconnectUseCase {
    private final GameRepository gameRepository;
    private final DisconnectScheduler disconnectScheduler;
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;

    public DisconnectUseCase(
            GameRepository gameRepository,
            DisconnectScheduler disconnectScheduler,
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        this.gameRepository = gameRepository;
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
        if (user == null) return Optional.empty();

        if (!user.isInGame()) {
            return Optional.empty();
        }

        Game game = gameRepository.find(user.getCurrentGameId());

        if (game == null) {
            user.leaveGame();
            userRepository.save(user);
            return Optional.empty();
        }

        Participant participant = game.getParticipantByUserId(userId);
        if (participant == null) return Optional.empty();

        disconnectScheduler.start(userId, game.getId());

        participant.disconnect();

        gameRepository.save(game);

        return buildReturn(game, userId);
    }

    private Optional<DisconnectParticipantOutput> buildReturn(Game game, String user) {
        return Optional.of(
                new DisconnectParticipantOutput(user, game)
        );
    }
}
