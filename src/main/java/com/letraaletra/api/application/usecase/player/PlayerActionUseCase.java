package com.letraaletra.api.application.usecase.player;

import com.letraaletra.api.application.command.actor.PlayerActionActorCommand;
import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.actor.PlayerActionResult;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerActionUseCase {
    private final TokenService tokenService;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public PlayerActionUseCase(
            TokenService tokenService,
            GameTimeoutManager gameTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager,
            UserRepository userRepository,
            GameRepository gameRepository
    ) {
        this.tokenService = tokenService;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public PlayerActionOutput execute(PlayerActionCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<PlayerActionResult> future = actor.enqueueCommand(new PlayerActionActorCommand(
                command.user(), command.action(), gameTimeoutManager, turnTimeoutManager
        ));

        PlayerActionResult result = future.join();

        if (result.gameOverResult().finished()) {
            if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
                gameActorManager.remove(gameId);

                removeAllPlayersFromGame(result.game());
            }

            GameOverResult gameOverResult = result.gameOverResult();

            updateStats(gameOverResult.winner(), true);
            updateStats(gameOverResult.loser(), false);

            gameRepository.save(result.game());
        }

        return buildOutput(result.game(), result.gameOverResult(), result.events());
    }

    private PlayerActionOutput buildOutput(Game game, GameOverResult gameOverResult, List<StateEvent> event) {
        return new PlayerActionOutput(
                game,
                event,
                gameOverResult.finished() ? Optional.of(gameOverResult) : Optional.empty()
        );
    }

    private void removeAllPlayersFromGame(Game game) {
        game.getParticipants().forEach(participant -> {
                Optional<User> user = userRepository.find(participant.getUserId());

                if (user.isPresent()) {
                    user.get().leaveGame();

                    userRepository.save(user.get());
                }
            }
        );
    }

    private void updateStats(Player player, boolean isWinner) {
        User user = userRepository.find(player.getUserId())
                .orElseThrow(UserNotFoundException::new);

        user.registerMatchResult(isWinner);

        userRepository.save(user);
    }
}
