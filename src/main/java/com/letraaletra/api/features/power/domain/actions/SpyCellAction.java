package com.letraaletra.api.features.power.domain.actions;

import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.PlayerSpiedEvent;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.power.domain.PowerType;
import com.letraaletra.api.features.game.domain.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.effect.SpyEffect;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;
import com.letraaletra.api.features.player.domain.exception.NotYourTurnException;
import com.letraaletra.api.features.player.domain.exception.PlayerNotInGameException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpyCellAction implements GameAction {
    private final String powerId;
    private final Position position;

    public SpyCellAction(String powerId, Position position) {
        this.powerId = powerId;
        this.position = position;
    }

    @Override
    public List<Event> execute(GameState state, UUID userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);
        validatePlayer(player);
        validatePower(player);

        player.resetPassedTurn();

        Cell cell = state.getBoard().getCell(position);
        validateCell(cell);

        player.removeFromInventoryOrThrow(powerId);

        player.applyEffect(new SpyEffect(position));

        return new ArrayList<>(List.of(new Event(
                StateEvent.PLAYER_SPIED,
                new PlayerSpiedEvent(
                        userId
                )
        )));
    }

    private void validatePlayerTurn(GameState state, UUID userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validateCell(Cell cell) {
        if (cell == null) {
            throw new InvalidPlayerActionException();
        }

        if (cell.isRevealed()) {
            throw new CellAlreadyRevealedException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.SPY) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }
}
