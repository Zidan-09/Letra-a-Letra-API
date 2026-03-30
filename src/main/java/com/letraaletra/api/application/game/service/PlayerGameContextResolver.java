package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.websocket.PlayerGameContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerGameContextResolver {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public PlayerGameContext resolve(String userId) {
        if (userId == null) return null;

        User user = userRepository.find(userId);
        if (user == null || !user.isInGame()) return null;

        Game game = gameRepository.find(user.getCurrentGameId());

        if (game == null) {
            user.leaveGame();
            userRepository.save(user);
            return null;
        }

        Participant participant = game.getParticipantByUserId(userId);
        if (participant == null) return null;

        return new PlayerGameContext(user, game, participant);
    }
}
