package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.game.domain.Game;

public class FindByCodeUseCase implements UseCase<FindByCodeInput, FindByCodeOutput> {
    private final GameQueryService gameQueryService;

    public FindByCodeUseCase(GameQueryService gameQueryService) {
        this.gameQueryService = gameQueryService;
    }

    public FindByCodeOutput execute(FindByCodeInput input) {
        Game game = gameQueryService.findByCode(input.code());

        validateGame(game);

        return buildOutput(game);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private FindByCodeOutput buildOutput(Game game) {
        return new FindByCodeOutput(
                game.getId().toString()
        );
    }
}
