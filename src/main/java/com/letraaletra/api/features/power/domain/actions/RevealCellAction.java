package com.letraaletra.api.features.power.domain.actions;

import com.letraaletra.api.features.game.domain.event.CellRevealedEvent;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.WordFoundedEvent;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.power.domain.PowerType;
import com.letraaletra.api.features.game.domain.board.cell.effect.CellEffect;
import com.letraaletra.api.features.game.domain.board.cell.effect.InteractResult;
import com.letraaletra.api.features.game.domain.board.word.Word;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.player.domain.exception.NotYourTurnException;

import java.util.*;

public class RevealCellAction implements GameAction {
    private final Position position;

    public RevealCellAction(Position position) {
        this.position = position;
    }

    @Override
    public List<Event> execute(GameState state, UUID userId) {
        validatePlayerTurn(state, userId);

        Cell cell = state.getBoard().getCell(position);

        List<Event> events = new ArrayList<>();

        Player player = state.getPlayerOrThrow(userId);

        player.resetPassedTurn();

        boolean canContinue = activateEffect(cell, player.getUserId(), events);

        if (!canContinue) return events;

        PowerType drop = cell.reveal(userId);
        addPower(drop, player);

        List<Event> found = checkCompletedWords(cell, userId, state);

        events.add(new Event(
                StateEvent.CELL_REVEALED,
                new CellRevealedEvent(
                        position,
                        userId.toString()
                )
        ));

        if (found != null) events.addAll(found);

        return events;
    }

    private List<Event> checkCompletedWords(Cell cell, UUID userId, GameState state) {
        List<Word> words = cell.getRelatedWords();

        if (words.isEmpty()) return null;

        Board board = state.getBoard();

        List<Event> wordsFoundEvent = new ArrayList<>();

        for (Word word : words) {
            List<Position> positions = word.getPositions();

            boolean isComplete = positions.stream().allMatch(pos -> board.getCell(pos).isRevealed());

            if (isComplete && word.markAsFound(userId)) {
                Player player = state.getPlayerOrThrow(userId);
                player.incrementScore();
                wordsFoundEvent.add(new Event(
                        StateEvent.WORD_FOUNDED,
                        new WordFoundedEvent(
                                positions.toArray(Position[]::new),
                                userId.toString()
                        )
                ));
            }
        }

        return !wordsFoundEvent.isEmpty() ? wordsFoundEvent : null;
    }

    private void validatePlayerTurn(GameState state, UUID userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void addPower(PowerType drop, Player player) {
        if (drop == null) return;

        player.addToInventory(drop);
    }

    private boolean activateEffect(Cell cell, UUID player, List<Event> events) {
        if (cell.hasEffect()) {
            CellEffect effect = cell.getEffect();
            InteractResult result = effect.onInteract(this, player, cell);

            events.add(result.event());

            return result.canContinue();
        }

        return true;
    }
}
