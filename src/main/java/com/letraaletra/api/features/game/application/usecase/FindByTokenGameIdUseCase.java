package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;

public class FindByTokenGameIdUseCase implements UseCase<FindByTokenInput, FindByTokenOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> actorManager;

    public FindByTokenGameIdUseCase(TokenService tokenService, ActorManager<Game> actorManager) {
        this.tokenService = tokenService;
        this.actorManager = actorManager;
    }

    public FindByTokenOutput execute(FindByTokenInput command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = actorManager.get(gameId);

        return buildReturn(actor.getGame(), command.token());
    }

    private FindByTokenOutput buildReturn(Game game, String token) {
        return new FindByTokenOutput(
                token,
                game
        );
    }
}
