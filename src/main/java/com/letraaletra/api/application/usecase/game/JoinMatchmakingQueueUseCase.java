package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.JoinMatchmakingCommand;
import com.letraaletra.api.application.output.game.JoinMatchmakingOutput;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.domain.game.*;
import com.letraaletra.api.domain.game.matchmaking.MatchmakingUser;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.factory.ParticipantFactory;
import com.letraaletra.api.domain.game.service.DefaultGameGenerator;
import com.letraaletra.api.domain.game.service.DefaultGameResult;
import com.letraaletra.api.domain.game.service.DefaultGameStateGenerator;
import com.letraaletra.api.domain.game.service.GenerateRoomCode;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JoinMatchmakingQueueUseCase {
    private final MatchmakingRepository matchmakingRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final DefaultGameStateGenerator defaultGameStateGenerator;
    private final DefaultGameGenerator defaultGameGenerator;
    private final PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase;
    private final GenerateRoomCode generateRoomCode;
    private final TokenService tokenService;
    private final TurnTimeoutManager turnTimeoutManager;

    private final Map<GameMode, Object> locks = new ConcurrentHashMap<>();

    public JoinMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository,
            GameRepository gameRepository,
            DefaultGameStateGenerator defaultGameStateGenerator,
            DefaultGameGenerator defaultGameGenerator,
            PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase,
            GenerateRoomCode generateRoomCode,
            TokenService tokenService,
            TurnTimeoutManager turnTimeoutManager
    ) {
        this.matchmakingRepository = matchmakingRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.defaultGameStateGenerator = defaultGameStateGenerator;
        this.defaultGameGenerator = defaultGameGenerator;
        this.pickRandomThemeWordsUseCase = pickRandomThemeWordsUseCase;
        this.generateRoomCode = generateRoomCode;
        this.tokenService = tokenService;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    public JoinMatchmakingOutput execute(JoinMatchmakingCommand command) {
        MatchmakingUser matchmakingUser = command.matchmakingUser();

        User user = userRepository.find(matchmakingUser.user());
        validateUser(user);

        Object lock = locks.computeIfAbsent(command.gameMode(), k -> new Object());

        synchronized (lock) {
            MatchmakingUser matchmakingOpponent = matchmakingRepository.poll(command.gameMode());

            if (matchmakingOpponent == null) {
                matchmakingRepository.add(matchmakingUser, command.gameMode());
                return buildOutput(new DefaultGameResult(null));
            }

            User opponent = userRepository.find(matchmakingOpponent.user());

            validateUser(opponent);

            Participant player1 = ParticipantFactory.fromUser(opponent, matchmakingOpponent.session());

            Participant player2 = ParticipantFactory.fromUser(user, matchmakingUser.session());

            DefaultGameResult result = defaultGameGenerator.generate(player1, player2, getCode());

            user.enterGame(result.game().getId());
            opponent.enterGame(result.game().getId());

            userRepository.save(user);
            userRepository.save(opponent);

            startDefaultGame(result.game(), command.gameMode());

            return buildOutput(result);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void startDefaultGame(Game game, GameMode gameMode) {
        List<String> words = pickRandomThemeWordsUseCase.execute();

        GameState state = defaultGameStateGenerator.generate(game, gameMode, words);

        game.setGameStatus(GameStatus.RUNNING);

        game.updateGameState(state);

        turnTimeoutManager.start(game);

        gameRepository.save(game);
    }

    private String getCode() {
        String code;

        do {
            code = generateRoomCode.execute();

        } while (gameRepository.existsByCode(code));

        return code;
    }

    private JoinMatchmakingOutput buildOutput(DefaultGameResult result) {
        return new JoinMatchmakingOutput(
                result.game() != null ? Optional.of(tokenService.generateToken(result.game().getId())) : Optional.empty(),
                result.game() != null ? Optional.of(result.game()) : Optional.empty()
        );
    }
}
