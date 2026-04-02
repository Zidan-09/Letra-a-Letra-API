package com.letraaletra.api.application.context;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.Optional;

public class PlayerGameContextFactory {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public PlayerGameContextFactory(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public Optional<PlayerGameContext> resolve(String userId) {
        if (userId == null) return Optional.empty();

        User user = userRepository.find(userId);
        if (user == null || !user.isInGame()) return Optional.empty();

        Game game = gameRepository.find(user.getCurrentGameId());

        if (game == null) {
            user.leaveGame();
            userRepository.save(user);
            return Optional.empty();
        }

        Participant participant = game.getParticipantByUserId(userId);
        if (participant == null) return Optional.empty();

        return Optional.of(new PlayerGameContext(game, participant));
    }
}
