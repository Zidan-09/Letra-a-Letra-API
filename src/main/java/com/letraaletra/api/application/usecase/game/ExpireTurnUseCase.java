package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.ExpireTurnActorCommand;
import com.letraaletra.api.application.command.game.ExpireTurnCommand;
import com.letraaletra.api.application.output.actor.ExpireTurnResult;
import com.letraaletra.api.application.output.game.ExpireTurnOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExpireTurnUseCase {
    private final ActorManager gameActorManager;
    private final GameTimeoutManager gameTimeoutManager;
    private final UserRepository userRepository;

    public ExpireTurnUseCase(
            ActorManager gameActorManager,
            GameTimeoutManager gameTimeoutManager,
            UserRepository userRepository
    ) {
        this.gameActorManager = gameActorManager;
        this.gameTimeoutManager = gameTimeoutManager;
        this.userRepository = userRepository;
    }

    public Optional<ExpireTurnOutput> execute(ExpireTurnCommand command) {
        Actor actor = gameActorManager.getOrCreate(command.gameId());

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

        if (expireTurn.removedBecauseAfk()) {
            User user = userRepository.find(expireTurn.whoPassed());
            if (user != null) {
                user.leaveGame();
                userRepository.save(user);
            }
        }

        if (expireTurn.gameOverResult().finished()) {
            for (Participant participant : expireTurn.game().getParticipants()) {
                User pUser = userRepository.find(participant.getUserId());
                if (pUser != null) {
                    pUser.leaveGame();
                    userRepository.save(pUser);
                }
            }
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
}
