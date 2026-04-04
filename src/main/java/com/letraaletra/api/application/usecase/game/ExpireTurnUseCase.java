package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.ExpireTurnCommand;
import com.letraaletra.api.application.output.game.ExpireTurnOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.repository.GameRepository;

import java.time.Instant;
import java.util.Optional;

public class ExpireTurnUseCase {
    private final GameRepository gameRepository;

    public ExpireTurnUseCase(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Optional<ExpireTurnOutput> execute(ExpireTurnCommand command) {
        Game game = gameRepository.find(command.gameId());

        if (game == null || game.getGameStatus() == GameStatus.WAITING) {
            return Optional.empty();
        }

        var state = game.getGameState();

        if (state.getVersion() != command.version()) {
            return Optional.empty();
        }

        Instant now = Instant.now();

        if (!state.isTurnExpired(now)) {
            return Optional.empty();
        }

        String whoPassed = state.currentPlayerTurn();

        state.nextTurn(now.plusSeconds(45));

        gameRepository.save(game);

        return buildOutput(whoPassed, game);
    }

    private Optional<ExpireTurnOutput> buildOutput(String whoPassed, Game game) {
        return Optional.of(
                new ExpireTurnOutput("TURN_EXPIRED", whoPassed, game.getGameState().currentPlayerTurn(), game)
        );
    }
}
