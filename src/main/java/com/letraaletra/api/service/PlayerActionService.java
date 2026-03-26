package com.letraaletra.api.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.dto.response.websocket.GameStateUpdatedWsResponse;
import com.letraaletra.api.exception.exceptions.*;
import com.letraaletra.api.infra.repository.GameRepository;
import com.letraaletra.api.service.actions.GameAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerActionService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BroadCastService broadCastService;

    @Autowired
    private GameStateAssembler gameStateAssembler;

    public void execute(String gameTokenId, String userId, GameAction action) {
        String gameId = tokenService.getTokenContent(gameTokenId);

        Game game = gameRepository.find(gameId);

        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus() != GameStatus.RUNNING) {
            throw new GameNotStartedException();
        }

        action.execute(game, userId);

        GameStateUpdatedWsResponse data = new GameStateUpdatedWsResponse(
                gameStateAssembler.get(game.getGameState())
        );

        broadCastService.broadCast(gameId, data);
    }
}
