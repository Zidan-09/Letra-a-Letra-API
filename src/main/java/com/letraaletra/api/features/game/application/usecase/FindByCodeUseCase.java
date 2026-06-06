package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;

public class FindByCodeUseCase implements UseCase<FindByCodeInput, FindByCodeOutput> {
    private final GameQueryService gameQueryService;
    private final TokenService tokenService;

    public FindByCodeUseCase(GameQueryService gameQueryService, TokenService tokenService) {
        this.gameQueryService = gameQueryService;
        this.tokenService = tokenService;
    }

    public FindByCodeOutput execute(FindByCodeInput command) {
        Game game = gameQueryService.findByCode(command.code());

        validateGame(game);

        String token = tokenService.generateToken(game.getId());

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
