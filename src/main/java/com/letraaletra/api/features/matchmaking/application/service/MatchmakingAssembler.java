package com.letraaletra.api.features.matchmaking.application.service;

import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.application.service.PickRandomThemeWordsService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.factory.DefaultGameFactory;
import com.letraaletra.api.features.game.domain.factory.DefaultGameResult;
import com.letraaletra.api.features.game.domain.factory.DefaultGameStateFactory;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;

import java.util.List;

public class MatchmakingAssembler {
    private final DefaultGameFactory gameFactory;
    private final DefaultGameStateFactory stateFactory;
    private final PickRandomThemeWordsService wordsService;
    private final GenerateRoomCode generateRoomCode;
    private final GameQueryService queryService;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActorManager<Game> actorManager;

    public MatchmakingAssembler(
            DefaultGameFactory gameFactory,
            DefaultGameStateFactory stateFactory,
            PickRandomThemeWordsService wordsService,
            GenerateRoomCode generateRoomCode,
            GameQueryService queryService,
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager
    ) {
        this.gameFactory = gameFactory;
        this.stateFactory = stateFactory;
        this.wordsService = wordsService;
        this.generateRoomCode = generateRoomCode;
        this.queryService = queryService;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.actorManager = actorManager;
    }

    public Game create(
            MatchmakingPair users,
            GameMode gameMode
    ) {
        User user1 = userRepository.find(users.first().userId())
                .orElseThrow(UserNotFoundException::new);

        User user2 = userRepository.find(users.second().userId())
                .orElseThrow(UserNotFoundException::new);

        Participant participant1 = Participant.create(user1, users.first().session());
        Participant participant2 = Participant.create(user2, users.second().session());

        String code = getCode();

        DefaultGameResult result = gameFactory.generate(participant1, participant2, code);

        actorManager.create(result.game().getId(), result.game());

        user1.enterGame(result.game().getId());
        user2.enterGame(result.game().getId());

        userRepository.save(user1);
        userRepository.save(user2);

        startDefaultGame(result.game(), gameMode);

        gameRepository.save(result.game());

        return result.game();
    }

    private void startDefaultGame(Game game, GameMode gameMode) {
        List<String> words = wordsService.execute();

        GameState state = stateFactory.generate(game, gameMode, words);

        game.setGameStatus(GameStatus.RUNNING);

        game.updateGameState(state);
    }

    private String getCode() {
        String code;

        do {
            code = generateRoomCode.execute();

        } while (queryService.existsByCode(code));

        return code;
    }
}
