package com.letraaletra.api.application.service;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

public class LeaveGameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public LeaveGameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public void leave(String gameId, String userId) {
        User user = userRepository.find(userId);
        validateUser(user);

        Game game = gameRepository.find(gameId);
        validateGame(game);

        Participant participant = game.getParticipantByUserId(userId);
        if (participant == null || participant.isConnected()) return;

        user.leaveGame();
        game.remove(userId);

        userRepository.save(user);

        if (game.getParticipants().isEmpty()) {
            gameRepository.removeByCode(game.getCode());
        } else {
            gameRepository.save(game);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }
}