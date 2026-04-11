package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.output.actor.ExpireTurnResult;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;

import java.time.Instant;
import java.util.Optional;

public class ExpireTurnActorCommand implements ActorCommand<Optional<ExpireTurnResult>> {
    private final int version;

    public ExpireTurnActorCommand(int version) {
        this.version = version;
    }

    @Override
    public Optional<ExpireTurnResult> execute(Game game) {
        if (game == null || game.getGameStatus() == GameStatus.WAITING) {
            return Optional.empty();
        }

        var state = game.getGameState();

        if (state.getVersion() != version) {
            return Optional.empty();
        }

        Instant now = Instant.now();

        if (!state.isTurnExpired(now)) {
            return Optional.empty();
        }

        String whoPassed = state.currentPlayerTurn();

        state.nextTurn(now.plusSeconds(45));

        return Optional.of(new ExpireTurnResult(whoPassed, game));
    }
}
