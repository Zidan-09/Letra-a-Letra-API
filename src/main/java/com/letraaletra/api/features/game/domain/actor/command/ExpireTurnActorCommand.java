package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

import java.time.Instant;
import java.util.Optional;

public class ExpireTurnActorCommand implements ActorCommand<Optional<ExpireTurnResult>> {
    private final int version;

    public ExpireTurnActorCommand(
            int version
    ) {
        this.version = version;
    }

    @Override
    public Optional<ExpireTurnResult> execute(Game game) {
        if (game == null || game.getGameStatus() == GameStatus.WAITING) {
            return Optional.empty();
        }

        GameState state = game.getGameState();

        if (state.getVersion() != version) {
            return Optional.empty();
        }

        Instant now = Instant.now();

        if (!state.isTurnExpired(now)) {
            return Optional.empty();
        }

        String whoPassed = state.currentPlayerTurn();

        Player player = state.getPlayerOrThrow(whoPassed);
        player.passedTurn();

        boolean shouldRemove = player.getPassedTurn() >= 3;

        if (shouldRemove) {
            game.remove(whoPassed);
        } else {
            state.nextTurn(now.plusSeconds(45));
        }

        GameOverResult gameOverResult = state.gameOverChecker();

        return Optional.of(new ExpireTurnResult(
                whoPassed,
                game,
                gameOverResult,
                shouldRemove
        ));
    }
}
