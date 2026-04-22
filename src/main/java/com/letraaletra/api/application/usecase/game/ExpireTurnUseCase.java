package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.ExpireTurnActorCommand;
import com.letraaletra.api.application.command.game.ExpireTurnCommand;
import com.letraaletra.api.application.output.actor.ExpireTurnResult;
import com.letraaletra.api.application.output.game.ExpireTurnOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExpireTurnUseCase {
    private final ActorManager<Game> gameActorManager;
    private final GameTimeoutManager gameTimeoutManager;
    private final UserRepository userRepository;

    public ExpireTurnUseCase(
            ActorManager<Game> gameActorManager,
            GameTimeoutManager gameTimeoutManager,
            UserRepository userRepository
    ) {
        this.gameActorManager = gameActorManager;
        this.gameTimeoutManager = gameTimeoutManager;
        this.userRepository = userRepository;
    }

    public Optional<ExpireTurnOutput> execute(ExpireTurnCommand command) {
        Actor actor = gameActorManager.get(command.gameId());

        CompletableFuture<Optional<ExpireTurnResult>> future = actor.enqueueCommand(
                new ExpireTurnActorCommand(command.version(), gameTimeoutManager)
        );

        Optional<ExpireTurnResult> result = future.join();

        removeUserRoomId(result.orElse(null));

        return result.flatMap(expireTurnResult -> buildOutput(
                expireTurnResult.whoPassed(),
                expireTurnResult.game(),
                expireTurnResult.gameOverResult(),
                expireTurnResult.removedBecauseAfk()
        ));
    }

    private void removeUserRoomId(ExpireTurnResult expireTurn) {
        if (expireTurn == null) return;

        if (expireTurn.gameOverResult().finished()) {
            handleGameOver(expireTurn);
        }

        if (expireTurn.removedBecauseAfk()) {
            handleAfkRemoval(expireTurn);
        }
    }

    private Optional<ExpireTurnOutput> buildOutput(String whoPassed, Game game, GameOverResult gameOverResult, boolean removedBecauseAfk) {
        return Optional.of(
                new ExpireTurnOutput(
                        "TURN_EXPIRED",
                        whoPassed,
                        game.getGameState().currentPlayerTurn(),
                        game,
                        gameOverResult,
                        removedBecauseAfk
                )
        );
    }

    private void updateStats(Player player, boolean isWinner) {
        User user = userRepository.find(player.getUserId())
                .orElseThrow(UserNotFoundException::new);

        user.registerMatchResult(isWinner);

        userRepository.save(user);
    }

    private User getUserOrThrow(String userId) {
        return userRepository.find(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private void handleAfkRemoval(ExpireTurnResult expireTurn) {
        User user = getUserOrThrow(expireTurn.whoPassed());

        user.leaveGame();
        userRepository.save(user);
    }

    private void handleGameOver(ExpireTurnResult expireTurn) {
        for (Participant participant : expireTurn.game().getParticipants()) {
            User user = getUserOrThrow(participant.getUserId());

            if (!expireTurn.game().getGameStatus().equals(GameStatus.WAITING)) {
                user.leaveGame();
            }

            userRepository.save(user);
        }

        GameOverResult result = expireTurn.gameOverResult();

        updateStats(result.winner(), true);
        updateStats(result.loser(), false);
    }
}
