package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.application.output.GetPublicGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import org.springframework.data.domain.Page;

public class GetPublicGamesUseCase implements UseCase<GetPublicGamesInput, GetPublicGamesOutput> {
    private final GameQueryService gameQueryService;

    public GetPublicGamesUseCase(GameQueryService gameQueryService) {
        this.gameQueryService = gameQueryService;
    }

    public GetPublicGamesOutput execute(GetPublicGamesInput input) {
        Page<Game> games = gameQueryService.getPublic(input);

        return new GetPublicGamesOutput(games);
    }
}
