package com.letraaletra.api.domain.game.actions;

import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.Cell;
import com.letraaletra.api.domain.board.Word;
import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.domain.game.exceptions.CellAlreadyRevealedException;
import com.letraaletra.api.domain.game.exceptions.NotYourTurnException;

import java.util.List;

public class RevealCellAction implements GameAction {
    private final Position position;

    public RevealCellAction(Position position) {
        this.position = position;
    }

    @Override
    public void execute(GameState state, String userId) {

        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }

        Cell cell = state.getBoard().getCell(position);

        if (cell.isRevealed()) {
            throw new CellAlreadyRevealedException();
        }

        cell.reveal(userId);

        checkCompletedWords(cell, userId, state);

        state.nextTurn();
    }

    private void checkCompletedWords(Cell cell, String userId, GameState state) {
        List<Word> words = cell.getRelatedWords();

        if (words.isEmpty()) {
            return;
        }

        Board board = state.getBoard();

        words.forEach(word -> {
            List<Position> positions = word.getPositions();

            boolean isComplete = positions.stream().allMatch(pos -> board.getCell(pos).isRevealed());

            if (isComplete) {
                Player player = state.getPlayerOrThrow(userId);

                word.markAsFound(userId);
                player.incrementScore();
            }
        });
    }
}
