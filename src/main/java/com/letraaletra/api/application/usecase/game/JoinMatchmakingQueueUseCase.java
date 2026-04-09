package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.JoinMatchmakingCommand;
import com.letraaletra.api.application.output.game.JoinMatchmakingOutput;
import com.letraaletra.api.domain.game.*;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.participant.factory.ParticipantFactory;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.Optional;

public class JoinMatchmakingQueueUseCase {
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;

    public JoinMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
    }

    public synchronized JoinMatchmakingOutput execute(JoinMatchmakingCommand command) {
        MatchmakingUser matchmakingUser = command.matchmakingUser();

        User user = userRepository.find(matchmakingUser.user());

        validateUser(user);

        boolean isEmptyQueue = matchmakingRepository.isEmpty();

        if (isEmptyQueue) {
            matchmakingRepository.add(matchmakingUser);

            return buildOutput(null);
        }

        Participant player2 = ParticipantFactory.fromUser(user, matchmakingUser.session(), ParticipantRole.PLAYER);

        MatchmakingUser matchmakingOpponent = matchmakingRepository.poll();

        User opponent = userRepository.find(matchmakingOpponent.user());

        validateUser(opponent);

        Participant player1 = ParticipantFactory.fromUser(opponent, matchmakingOpponent.session(), ParticipantRole.PLAYER);

        game.join(player2);

        user.enterGame(game.getId());
        opponent.enterGame(game.getId());

        userRepository.save(user);
        userRepository.save(opponent);

        return buildOutput(gameStarted);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private JoinMatchmakingOutput buildOutput(Game game) {
        return new JoinMatchmakingOutput(game != null ? Optional.of(game) : Optional.empty());
    }
}
