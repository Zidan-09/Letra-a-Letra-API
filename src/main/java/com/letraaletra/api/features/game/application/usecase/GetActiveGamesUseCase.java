package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.game.application.input.GetActiveGamesInput;
import com.letraaletra.api.features.game.application.output.GetActiveGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetActiveGamesUseCase implements UseCase<GetActiveGamesInput, GetActiveGamesOutput> {
    private final GameQueryService gameQueryService;
    private final AdminChecker adminChecker;

    public GetActiveGamesUseCase(
            GameQueryService gameQueryService,
            AdminChecker adminChecker
    ) {
        this.gameQueryService = gameQueryService;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetActiveGamesOutput execute(GetActiveGamesInput input) {
        adminChecker.check(input.principal(), PermissionKey.GAME, PermissionAction.VIEW);

        Page<Game> games = gameQueryService
                .getAllActiveGames(input);

        return new GetActiveGamesOutput(games);
    }
}
