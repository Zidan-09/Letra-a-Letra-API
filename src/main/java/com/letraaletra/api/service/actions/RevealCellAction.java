package com.letraaletra.api.service.actions;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.Cell;
import com.letraaletra.api.domain.board.Word;
import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.exception.exceptions.CellAlreadyRevealedException;
import com.letraaletra.api.exception.exceptions.NotYourTurnException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RevealCellAction implements GameAction {
    private final Position position;

    public RevealCellAction(Position position) {
        this.position = position;
    }

    @Override
    public void execute(Game game, String userId) {
        GameState gameState = game.getGameState();

        if (!gameState.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }

        Cell cell = gameState.getBoard().getCell(position);

        if (cell.isRevealed()) {
            throw new CellAlreadyRevealedException();
        }

        cell.reveal(userId);

        checkCompletedWords(cell, userId, gameState);

        gameState.nextTurn();
    }

    private void checkCompletedWords(Cell cell, String userId, GameState gameState) {
        List<Word> words = cell.getRelatedWords();

        if (words.isEmpty()) {
            return;
        }

        Board board = gameState.getBoard();

        words.forEach(word -> {
            List<Position> positions = word.getPositions();

            boolean isComplete = positions.stream().allMatch(pos -> board.getCell(pos).isRevealed());

            if (isComplete) {
                Player player = gameState.getPlayers().get(userId);

                if (player == null) {
                    throw new RuntimeException("");
                }

                word.markAsFound(userId);
                player.incrementScore();
            }
        });
    }
}
