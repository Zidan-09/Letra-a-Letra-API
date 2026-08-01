package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class ExpireTurnActorCommand implements ActorCommand<Optional<ExpireTurnResult>> {
    private final UserRepository userRepository;
    private final int version;

    public ExpireTurnActorCommand(
            UserRepository userRepository,
            int version
    ) {
        this.userRepository = userRepository;
        this.version = version;
    }

    @Override
    public Optional<ExpireTurnResult> execute(Game game) {
        if (game.getGameStatus() != GameStatus.RUNNING) {
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

        UUID whoPassed = state.currentPlayerTurn();

        User user = userRepository.find(whoPassed)
                .orElseThrow(UserNotFoundException::new);

        Player player = state.getPlayerOrThrow(whoPassed);
        player.passedTurn();

        Optional<GameOver> gameOver = state.gameOverBecauseAfk();

        if (gameOver.isPresent()) {
            game.remove(whoPassed);
            user.leaveGame();

            if (game.getGameType() == GameType.CUSTOM) {
                game.setGameStatus(GameStatus.WAITING);
            } else {
                game.setGameStatus(GameStatus.CLOSED);
            }

            userRepository.save(user);

        } else {
            state.nextTurn(now.plusSeconds(45));
        }

        return Optional.of(new ExpireTurnResult(
                whoPassed,
                game,
                gameOver
        ));
    }
}
