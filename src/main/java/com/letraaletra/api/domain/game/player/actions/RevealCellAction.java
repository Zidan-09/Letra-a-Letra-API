package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.word.Word;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;

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
