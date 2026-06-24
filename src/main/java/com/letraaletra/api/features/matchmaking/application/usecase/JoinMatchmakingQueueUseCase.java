package com.letraaletra.api.features.matchmaking.application.usecase;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.matchmaking.application.output.JoinMatchmakingOutput;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.service.PickRandomThemeWordsService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.matchmaking.MatchmakingUser;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.factory.ParticipantFactory;
import com.letraaletra.api.features.game.domain.factory.DefaultGameFactory;
import com.letraaletra.api.features.game.domain.factory.DefaultGameResult;
import com.letraaletra.api.features.game.domain.factory.DefaultGameStateFactory;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JoinMatchmakingQueueUseCase implements UseCase<JoinMatchmakingInput, JoinMatchmakingOutput> {
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameQueryService gameQueryService;
    private final DefaultGameStateFactory defaultGameStateFactory;
    private final DefaultGameFactory defaultGameFactory;
    private final PickRandomThemeWordsService pickRandomThemeWordsService;
    private final GenerateRoomCode generateRoomCode;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> actorManager;

    private final Map<GameMode, Object> locks = new ConcurrentHashMap<>();

    public JoinMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            GameQueryService gameQueryService,
            DefaultGameStateFactory defaultGameStateFactory,
            DefaultGameFactory defaultGameFactory,
            PickRandomThemeWordsService pickRandomThemeWordsService,
            GenerateRoomCode generateRoomCode,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> actorManager
    ) {
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameQueryService = gameQueryService;
        this.defaultGameStateFactory = defaultGameStateFactory;
        this.defaultGameFactory = defaultGameFactory;
        this.pickRandomThemeWordsService = pickRandomThemeWordsService;
        this.generateRoomCode = generateRoomCode;
        this.turnTimeoutManager = turnTimeoutManager;
        this.actorManager = actorManager;
    }

    public JoinMatchmakingOutput execute(JoinMatchmakingInput input) {
        MatchmakingUser matchmakingUser = input.matchmakingUser();

        User user = userRepository.find(matchmakingUser.user()).orElse(null);
        validateUser(user);

        Object lock = locks.computeIfAbsent(input.gameMode(), k -> new Object());

        synchronized (lock) {
            MatchmakingUser matchmakingOpponent = matchmakingRepository.poll(input.gameMode());

            if (matchmakingOpponent == null) {
                matchmakingRepository.add(matchmakingUser, input.gameMode());
                return buildOutput(new DefaultGameResult(null));
            }

            User opponent = userRepository.find(matchmakingOpponent.user()).orElse(null);

            validateUser(opponent);

            Participant player1 = ParticipantFactory.fromUser(opponent, matchmakingOpponent.session());

            Participant player2 = ParticipantFactory.fromUser(user, matchmakingUser.session());

            DefaultGameResult result = defaultGameFactory.generate(player1, player2, getCode());

            gameRepository.save(result.game());

            actorManager.create(result.game().getId(), result.game());

            user.enterGame(result.game().getId());
            opponent.enterGame(result.game().getId());

            userRepository.save(user);
            userRepository.save(opponent);

            startDefaultGame(result.game(), input.gameMode());

            return buildOutput(result);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void startDefaultGame(Game game, GameMode gameMode) {
        List<String> words = pickRandomThemeWordsService.execute();

        GameState state = defaultGameStateFactory.generate(game, gameMode, words);

        game.setGameStatus(GameStatus.RUNNING);

        game.updateGameState(state);

        turnTimeoutManager.start(game);

        gameRepository.save(game);
    }

    private String getCode() {
        String code;

        do {
            code = generateRoomCode.execute();

        } while (gameQueryService.existsByCode(code));

        return code;
    }

    private JoinMatchmakingOutput buildOutput(DefaultGameResult result) {
        return new JoinMatchmakingOutput(
                result.game() != null ? Optional.of(result.game()) : Optional.empty()
        );
    }
}
