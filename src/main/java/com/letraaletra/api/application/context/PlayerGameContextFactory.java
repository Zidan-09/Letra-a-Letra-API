package com.letraaletra.api.application.context;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.Optional;

public class PlayerGameContextFactory {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MatchmakingRepository matchmakingRepository;

    public PlayerGameContextFactory(GameRepository gameRepository, UserRepository userRepository, MatchmakingRepository matchmakingRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.matchmakingRepository = matchmakingRepository;
    }

    public Optional<PlayerGameContext> resolve(String userId) {
        if (userId == null) return Optional.empty();

        User user = userRepository.find(userId);
        if (user == null || !user.isInGame()) return Optional.empty();

        if (matchmakingRepository.onQueue(userId)) {
            matchmakingRepository.removeById(userId);
        }

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
