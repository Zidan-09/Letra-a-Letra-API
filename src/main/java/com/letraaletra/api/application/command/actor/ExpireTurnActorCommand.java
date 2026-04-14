package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.output.actor.ExpireTurnResult;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.GameType;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.time.Instant;
import java.util.Optional;

public class ExpireTurnActorCommand implements ActorCommand<Optional<ExpireTurnResult>> {
    private final int version;
    private final GameTimeoutManager gameTimeoutManager;

    public ExpireTurnActorCommand(int version, GameTimeoutManager gameTimeoutManager) {
        this.version = version;
        this.gameTimeoutManager = gameTimeoutManager;
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

        GameOverResult gameOverResultBefore = state.gameOverChecker();

        if (shouldRemove) {
            game.remove(whoPassed);

            GameOverResult gameOverResultAfter = state.gameOverChecker();

            if (gameOverResultAfter.finished()) {
                if (game.getGameType().equals(GameType.CUSTOM)) {
                    game.setGameStatus(GameStatus.WAITING);
                    gameTimeoutManager.start(game);
                }
            }

            return Optional.of(new ExpireTurnResult(
                    whoPassed,
                    game,
                    gameOverResultAfter,
                    true
            ));
        }

        if (gameOverResultBefore.finished()) {
            if (game.getGameType().equals(GameType.CUSTOM)) {
                game.setGameStatus(GameStatus.WAITING);
                gameTimeoutManager.start(game);
            }

            return Optional.of(new ExpireTurnResult(
                    whoPassed,
                    game,
                    gameOverResultBefore,
                    false
            ));
        }

        state.nextTurn(now.plusSeconds(45));

        return Optional.of(new ExpireTurnResult(
                whoPassed,
                game,
                gameOverResultBefore,
                false
        ));
    }
}
