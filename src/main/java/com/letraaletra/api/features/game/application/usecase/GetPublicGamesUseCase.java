package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.shared.application.usecase.UseCaseWithoutInput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.domain.security.TokenService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GetPublicGamesUseCase implements UseCaseWithoutInput<GetGamesOutput> {
    private final GameQueryService gameQueryService;
    private final TokenService tokenService;

    public GetPublicGamesUseCase(GameQueryService gameQueryService, TokenService tokenService) {
        this.gameQueryService = gameQueryService;
        this.tokenService = tokenService;
    }

    public GetGamesOutput execute() {
        List<Game> games = gameQueryService.getPublic();

        Map<String, String> tokens = new HashMap<>();

        for (Game game : games) {
            String token = tokenService.generateToken(UUID.fromString(game.getId()));

            tokens.put(game.getId(), token);
        }

        return buildOutput(games, tokens);
    }

    private GetGamesOutput buildOutput(List<Game> games, Map<String, String> tokens) {
        return new GetGamesOutput(
                games,
                tokens
        );
    }
}
