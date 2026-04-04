package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.JoinGameCommand;
import com.letraaletra.api.application.output.game.JoinGameOutput;
import com.letraaletra.api.domain.game.exception.UserBannedException;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.factory.ParticipantFactory;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

public class JoinGameUseCase {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public JoinGameUseCase(GameRepository gameRepository, UserRepository userRepository, TokenService tokenService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public JoinGameOutput execute(JoinGameCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);

        User user = userRepository.find(command.user());

        validateUser(user);
        checkIfBlackListed(game, command.user());

        ParticipantRole role = game.nextParticipantRole();

        Participant participant = ParticipantFactory.fromUser(user, command.session(), role);

        try {
            user.enterGame(gameId);
            game.join(participant);
        } catch (Exception e) {
            user.leaveGame();
            throw e;
        }

        userRepository.save(user);
        gameRepository.save(game);

        return buildReturn(command.token(), game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void checkIfBlackListed(Game game, String userId) {
        if (game.isBlackListed(userId)) {
            throw new UserBannedException();
        }
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private JoinGameOutput buildReturn(String token, Game game) {
        return new JoinGameOutput(token, game);
    }
}
