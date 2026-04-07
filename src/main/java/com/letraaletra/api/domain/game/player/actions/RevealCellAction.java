package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.board.Board;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.cell.effect.CellEffect;
import com.letraaletra.api.domain.game.board.word.Word;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RevealCellAction implements GameAction {
    private final Position position;

    public RevealCellAction(Position position) {
        this.position = position;
    }

    @Override
    public Optional<List<StateEvent>> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Cell cell = state.getBoard().getCell(position);

        boolean canContinue = activateEffect(cell, userId);

        if (!canContinue) return Optional.empty();

        PowerType drop = cell.reveal(userId);
        addPower(drop, state.getPlayerOrThrow(userId));

        Optional<StateEvent> found = checkCompletedWords(cell, userId, state);

        List<StateEvent> events = new ArrayList<>();
        events.add(StateEvent.CELL_REVEALED);

        found.ifPresent(events::add);

        return Optional.of(events);
    }

    private Optional<StateEvent> checkCompletedWords(Cell cell, String userId, GameState state) {
        List<Word> words = cell.getRelatedWords();

        if (words.isEmpty()) {
            return Optional.empty();
        }

        Board board = state.getBoard();

        int wordsFound = 0;

        for (Word word : words) {
            List<Position> positions = word.getPositions();

            boolean isComplete = positions.stream().allMatch(pos -> board.getCell(pos).isRevealed());

            if (isComplete) {
                Player player = state.getPlayerOrThrow(userId);

                word.markAsFound(userId);
                player.incrementScore();
                wordsFound++;
            }
        }

        if (wordsFound > 0) {
            return Optional.of(StateEvent.WORD_FOUNDED);
        }

        return Optional.empty();
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void addPower(PowerType drop, Player player) {
        if (drop == null) return;

        player.addToInventory(drop);
    }

    private boolean activateEffect(Cell cell, String player) {
        if (cell.hasEffect()) {
            CellEffect effect = cell.getEffect();
            return effect.onInteract(this, player, cell);
        }

        return true;
    }
}
