package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;

public class FindByTokenGameIdUseCase implements UseCase<FindByTokenInput, FindByTokenOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> actorManager;

    public FindByTokenGameIdUseCase(TokenService tokenService, ActorManager<Game> actorManager) {
        this.tokenService = tokenService;
        this.actorManager = actorManager;
    }

    public FindByTokenOutput execute(FindByTokenInput input) {
        UUID gameId = tokenService.getTokenContent(input.token());

        Actor actor = actorManager.get(gameId.toString());

        return buildOutput(actor.getGame(), input.token());
    }

    private FindByTokenOutput buildOutput(Game game, String token) {
        return new FindByTokenOutput(
                token,
                game
        );
    }
}
