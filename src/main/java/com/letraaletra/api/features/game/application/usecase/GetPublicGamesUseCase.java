package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.List;

public class GetPublicGamesUseCase implements UseCase<Void, GetGamesOutput> {
    private final GameQueryService gameQueryService;

    public GetPublicGamesUseCase(GameQueryService gameQueryService) {
        this.gameQueryService = gameQueryService;
    }

    public GetGamesOutput execute(Void input) {
        List<Game> games = gameQueryService.getPublic();

        return buildOutput(games);
    }

    private GetGamesOutput buildOutput(List<Game> games) {
        return new GetGamesOutput(
                games
        );
    }
}
