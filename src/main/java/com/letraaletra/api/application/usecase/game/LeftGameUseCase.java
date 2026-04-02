package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.LeftGameCommand;
import com.letraaletra.api.application.output.game.LeftGameOutput;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.exception.UserNotInGameException;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeftGameUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    public LeftGameOutput execute(LeftGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);

        Participant participant = game.getParticipant(command.session());

        validateParticipant(participant);

        User user = userRepository.find(participant.getUserId());

        validateUser(user);

        user.leaveGame();
        game.remove(participant.getUserId());

        if (game.getParticipants().isEmpty()) {
            gameRepository.removeByCode(game.getCode());
        } else {
            gameRepository.save(game);
        }

        userRepository.save(user);

        return buildReturn(game, command.token());
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }

    private LeftGameOutput buildReturn(Game game, String token) {
        return new LeftGameOutput(
                token,
                game
        );
    }
}
