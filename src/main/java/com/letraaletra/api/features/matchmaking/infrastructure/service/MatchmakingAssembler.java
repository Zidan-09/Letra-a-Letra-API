package com.letraaletra.api.features.matchmaking.infrastructure.service;

import com.letraaletra.api.features.game.application.port.RoomCodeService;
import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.JoinGameActorCommand;
import com.letraaletra.api.features.game.domain.actor.command.StartMatchGameActorCommand;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.factory.GameFactory;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.application.port.GameAssemblerService;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.domain.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MatchmakingAssembler implements GameAssemblerService {
    private final SelectThemeService themeService;
    private final RoomCodeService roomCodeService;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActorManager<Game> actorManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public Game create(
            MatchmakingPair users,
            GameMode gameMode,
            QueueType queueType
    ) {
        List<User> userList = userRepository.findUsersById(
                List.of(users.first().userId(), users.second().userId())
        );

        User user1 = userList.stream()
                .filter(u -> u.getId().equals(users.first().userId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        User user2 = userList.stream()
                .filter(u -> u.getId().equals(users.second().userId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        String code = roomCodeService.generate();

        Game game = queueType.equals(QueueType.RANKING) ?
                GameFactory.rank(code) :
                GameFactory.match(code);

        actorManager.create(game.getId(), game);

        Actor actor = actorManager.get(game.getId());

        CompletableFuture<Game> future = actor.enqueueCommand(new JoinGameActorCommand(
                user1, users.first().session()
        ));

        future.join();

        future = actor.enqueueCommand(new JoinGameActorCommand(
                user2, users.second().session()
        ));

        future.join();

        List<String> words = themeService.select();

        Board board = BoardGenerator.generate(words, gameMode);

        future = actor.enqueueCommand(new StartMatchGameActorCommand(
                board,
                turnTimeoutManager
        ));

        game = future.join();

        userRepository.saveAll(List.of(user1, user2));
        gameRepository.save(game);

        return game;
    }
}
