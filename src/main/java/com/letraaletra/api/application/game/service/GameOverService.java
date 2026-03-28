package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.game.service.GameOverChecker;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.presentation.dto.mappers.PlayerDTOMapper;
import com.letraaletra.api.presentation.dto.response.game.GameOverDTO;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameOverWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class GameOverService {
    @Autowired
    private GameOverChecker checker;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerDTOMapper playerDTOMapper;

    public Optional<GameOverWsResponse> buildIfFinished(Game game) {
        GameState state = game.getGameState();

        GameOverResult result = checker.evaluate(state);

        if (!result.finished()) {
            return Optional.empty();
        }

        return Optional.of(buildResponse(result));
    }

    private GameOverWsResponse buildResponse(GameOverResult result) {
        return new GameOverWsResponse(
                new GameOverDTO(
                        getPlayerDTO(result.winner()),
                        getPlayerDTO(result.loser())
                )
        );
    }

    private PlayerDTO getPlayerDTO(Player player) {
        if (player == null) {
            return null;
        }

        String id = player.getUserId();

        User user = userRepository.find(id);

        if (user == null) {
            throw new UserNotFoundException();
        }

        return playerDTOMapper.toDTO(player, user);
    }
}
