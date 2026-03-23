package com.letraaletra.api.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Cell;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.dto.response.game.GameStateDTO;
import com.letraaletra.api.exception.exceptions.*;
import com.letraaletra.api.infra.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameStateService {

    @Autowired
    private GameRepository gameRepository;

    public GameStateDTO revealCell(String gameId, String playerId, Position position) {
        Game game = gameRepository.find(gameId);

        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus() != GameStatus.RUNNING) {
            throw new GameNotStarted();
        }

        GameState gameState = game.getGameState();

        if (!Objects.equals(gameState.currentPlayerTurn(), playerId)) {
            throw new NotYourTurnException();
        }

        // Validações de poder futuramente

        Cell cell = gameState.getBoard().getCellOfGrid(position);

        if (cell == null) {
            throw new InvalidPositionException();
        }

        if (cell.isRevealed()) {
            throw new CellAlreadyRevealedException();
        }

        // Validação de poder aplicado na célula futuramente

        cell.reveal(playerId);

        return game.getGameStateToSend();
    }
}
