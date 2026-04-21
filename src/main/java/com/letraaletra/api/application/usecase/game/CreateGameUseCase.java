package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.CreateGameCommand;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.application.port.GameQueryService;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.domain.game.GameType;
import com.letraaletra.api.domain.game.service.GenerateRoomCode;
import com.letraaletra.api.application.output.game.CreateGameOutput;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.factory.ParticipantFactory;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

import java.util.UUID;

public class CreateGameUseCase {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActorManager actorManager;
    private final GameQueryService gameQueryService;
    private final GameTimeoutManager gameTimeoutManager;
    private final TokenService tokenService;
    private final GenerateRoomCode generateRoomCode;

    public CreateGameUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager actorManager,
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

    public CreateGameOutput execute(CreateGameCommand command) {
        String gameId = UUID.randomUUID().toString();

        User user = userRepository.find(command.user()).orElse(null);

        validateUser(user);

        Participant host = ParticipantFactory.fromUser(user, command.session());

        String code = getCode();

        Game game = new Game(gameId, code, command.name(), command.settings(), host, GameType.CUSTOM);

        user.enterGame(gameId);

        userRepository.save(user);
        gameRepository.save(game);

        actorManager.create(gameId);

        String tokenGameId = tokenService.generateToken(gameId);

        gameTimeoutManager.start(game);

        return buildReturn(tokenGameId, game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
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
