package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.user.application.port.UserStatsService;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameOverHandler implements GameOverService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;
    private final GameTimeoutManager gameTimeoutManager;
    private final UserStatsService userStatsService;

    @Override
    public void handle(Game game, GameOver result) {
        List<User> userList = userRepository.findUsersById(List.of(
                result.winner().getUserId(),
                result.loser().getUserId()
        ));

        User userWinner = userList.stream()
                .filter(u -> u.getUserId().equals(result.winner().getUserId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        User userLoser = userList.stream()
                .filter(u -> u.getUserId().equals(result.loser().getUserId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        userStatsService.update(userWinner, true);
        userStatsService.update(userLoser, false);

        if (game.getGameType().equals(GameType.CUSTOM)) {
            game.setGameStatus(GameStatus.WAITING);
            gameTimeoutManager.start(game);
        } else {
            game.setGameStatus(GameStatus.CLOSED);
        }

        if (game.getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(game.getId());
        }

        userRepository.saveAll(List.of(userWinner, userLoser));
        gameRepository.save(game);
    }
}
