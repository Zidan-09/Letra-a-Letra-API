package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.ReconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.ReconnectParticipantOutput;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.EnumSet;
import java.util.Optional;

public class ReconnectUseCase {
    private final GameRepository gameRepository;
    private final DisconnectScheduler disconnectScheduler;
    private final UserRepository userRepository;

    public ReconnectUseCase(GameRepository gameRepository, DisconnectScheduler disconnectScheduler, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.disconnectScheduler = disconnectScheduler;
        this.userRepository = userRepository;
    }

    public Optional<ReconnectParticipantOutput> execute(ReconnectParticipantCommand command) {
        String userId = command.user();
        if (userId == null) return Optional.empty();

        User user = userRepository.find(userId).orElse(null);
        if (user == null) return Optional.empty();

        if (user.isNotInGame()) {
            return Optional.empty();
        }

        Game game = gameRepository.find(user.getCurrentGameId());

        if (game == null || !EnumSet.of(GameStatus.RUNNING, GameStatus.WAITING)
                .contains(game.getGameStatus())) {
            user.leaveGame();
            userRepository.save(user);
            return Optional.empty();
        }

        Participant participant = game.getParticipantByUserId(userId);
        if (participant == null) return Optional.empty();

        disconnectScheduler.cancel(userId, game.getId());

        game.reconnect(userId, command.session());

        gameRepository.save(game);

        return buildReturn(game);
    }

    private Optional<ReconnectParticipantOutput> buildReturn(Game game) {
        return Optional.of(new ReconnectParticipantOutput(
                game
        ));
    }
}
