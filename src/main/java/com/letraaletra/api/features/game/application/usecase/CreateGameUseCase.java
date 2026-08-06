package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.port.RoomCodeService;
import com.letraaletra.api.features.game.domain.factory.GameFactory;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;

public class CreateGameUseCase implements UseCase<CreateGameInput, CreateGameOutput> {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActorManager<Game> actorManager;
    private final GameTimeoutManager gameTimeoutManager;
    private final RoomCodeService roomCodeService;

    public CreateGameUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            RoomCodeService roomCodeService
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.actorManager = actorManager;
        this.gameTimeoutManager = gameTimeoutManager;
        this.roomCodeService = roomCodeService;
    }

    @Override
    public CreateGameOutput execute(CreateGameInput input) {
        User user = userRepository.find(input.user())
                .orElseThrow(UserNotFoundException::new);

        String code = roomCodeService.generate();

        Game game = GameFactory.custom(code, input.settings(), input.name());

        game.join(user, input.session());
        user.enterGame(game.getId());

        userRepository.save(user);
        gameRepository.save(game);

        actorManager.create(game.getId(), game);

        gameTimeoutManager.start(game);

        return new CreateGameOutput(game);
    }
}
