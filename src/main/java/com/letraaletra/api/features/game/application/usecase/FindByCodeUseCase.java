package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;

public class FindByCodeUseCase implements UseCase<FindByCodeInput, FindByCodeOutput> {
    private final GameQueryService gameQueryService;
    private final TokenService tokenService;

    public FindByCodeUseCase(GameQueryService gameQueryService, TokenService tokenService) {
        this.gameQueryService = gameQueryService;
        this.tokenService = tokenService;
    }

    public FindByCodeOutput execute(FindByCodeInput input) {
        Game game = gameQueryService.findByCode(input.code());

        validateGame(game);

        String token = tokenService.generateToken(UUID.fromString(game.getId()));

        return buildReturn(token);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private FindByCodeOutput buildReturn(String token) {
        return new FindByCodeOutput(
                token
        );
    }
}
