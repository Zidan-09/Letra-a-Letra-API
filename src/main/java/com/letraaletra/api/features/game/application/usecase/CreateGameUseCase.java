package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.factory.ParticipantFactory;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.UUID;

public class CreateGameUseCase implements UseCase<CreateGameInput, CreateGameOutput> {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActorManager<Game> actorManager;
    private final GameQueryService gameQueryService;
    private final GameTimeoutManager gameTimeoutManager;
    private final TokenService tokenService;
    private final GenerateRoomCode generateRoomCode;

    public CreateGameUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            GameQueryService gameQueryService,
            TokenService tokenService,
            GenerateRoomCode generateRoomCode
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.actorManager = actorManager;
        this.gameTimeoutManager = gameTimeoutManager;
        this.gameQueryService = gameQueryService;
        this.tokenService = tokenService;
        this.generateRoomCode = generateRoomCode;
    }

    public CreateGameOutput execute(CreateGameInput input) {
        String gameId = UUID.randomUUID().toString();

        User user = userRepository.find(input.user()).orElse(null);

        validateUser(user);

        Participant host = ParticipantFactory.fromUser(user, input.session());

        String code = getCode();

        Game game = new Game(gameId, code, input.name(), input.settings(), host, GameType.CUSTOM);

        user.enterGame(gameId);

        userRepository.save(user);
        gameRepository.save(game);

        actorManager.create(gameId, game);

        String tokenGameId = tokenService.generateToken(gameId);

        gameTimeoutManager.start(game);

        return buildReturn(tokenGameId, game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.isNotInGame()) {
            throw new UserAlreadyInGameException();
        }
    }

    private String getCode() {
        String code;

        do {
            code = generateRoomCode.execute();

        } while (gameQueryService.existsByCode(code));

        return code;
    }

    private CreateGameOutput buildReturn(String token, Game game) {
        return new CreateGameOutput(token, game);
    }
}
