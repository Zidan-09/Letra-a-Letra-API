package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.GameOverService;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.presentation.dto.mappers.GameStateResponseAssembler;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.presentation.dto.response.websocket.GameStateUpdatedWsResponse;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.GameNotStartedException;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.game.actions.GameAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlayerActionUseCase {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameOverService gameOverService;

    @Autowired
    private BroadcastService broadCast;

    @Autowired
    private GameStateResponseAssembler gameStateResponseAssembler;

    public void execute(String gameTokenId, String userId, GameAction action) {
        String gameId = tokenService.getTokenContent(gameTokenId);

        Game game = gameRepository.find(gameId);

        validateGame(game);

        GameState state = game.getGameState();

        action.execute(state, userId);

        gameRepository.save(game);

        List<String> playerIds = state.getPlayerIds();

        Map<String, User> users = userRepository.findMapByIds(playerIds);

        GameStateUpdatedWsResponse data = buildResponse(state, users);
        broadCast.send(gameId, data);

        checkGameOver(game);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus() != GameStatus.RUNNING) {
            throw new GameNotStartedException();
        }
    }

    private GameStateUpdatedWsResponse buildResponse(GameState state, Map<String, User> users) {
        return new GameStateUpdatedWsResponse(
                gameStateResponseAssembler.get(state, users)
        );
    }

    private void checkGameOver(Game game) {
        gameOverService.buildIfFinished(game)
                .ifPresent(response -> broadCast.send(game.getId(), response));
    }
}
