package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.GetActiveGamesInput;
import com.letraaletra.api.features.game.application.output.GetActiveGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

import java.util.List;

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
        adminChecker.check(input.auth());

        Page<GameHistory> games = gameQueryService
                .getAllActiveGames(input)
                .map(this::toHistory);

        return new GetActiveGamesOutput(games);
    }

    private GameHistory toHistory(Game game) {
        return new GameHistory(
                game.getId(),
                game.getRoomName(),
                game.getGameType(),
                game.getGameStatus(),
                List.of()
        );
    }
}
