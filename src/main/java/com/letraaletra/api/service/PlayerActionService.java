package com.letraaletra.api.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Cell;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.dto.response.websocket.GameStateUpdatedWsResponse;
import com.letraaletra.api.exception.exceptions.*;
import com.letraaletra.api.infra.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public void revealCell(String gameTokenId, String token, Position position) {
        String gameId = tokenService.getTokenContent(gameTokenId);
        String userId = tokenService.getTokenContent(token);

        Game game = gameRepository.find(gameId);

        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus() != GameStatus.RUNNING) {
            throw new GameNotStarted();
        }

        GameState gameState = game.getGameState();

        if (!Objects.equals(gameState.currentPlayerTurn(), userId)) {
            throw new NotYourTurnException();
        }

        // Power validation in future

        Cell cell = gameState.getBoard().getCellOfGrid(position);

        if (cell == null) {
            throw new InvalidPositionException();
        }

        if (cell.isRevealed()) {
            throw new CellAlreadyRevealedException();
        }

        // Power on Cell validation in future

        cell.reveal(userId);

        GameStateUpdatedWsResponse data = new GameStateUpdatedWsResponse(
                gameStateAssembler.get(gameState)
        );

        broadCastService.broadCast(gameId, data);
    }
}
