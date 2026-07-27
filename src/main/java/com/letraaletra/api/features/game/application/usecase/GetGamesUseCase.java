package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.GetGamesInput;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.domain.GamesPage;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetGamesUseCase implements UseCase<GetGamesInput, GetGamesOutput> {
    private final GameRepository gameRepository;
    private final AdminChecker adminChecker;

    public GetGamesUseCase(
            GameRepository gameRepository,
            AdminChecker adminChecker
    ) {
        this.gameRepository = gameRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetGamesOutput execute(GetGamesInput input) {
        adminChecker.check(input.principal());

        Page<GameHistory> games = gameRepository.get(
                new GamesPage(
                        input.page(),
                        input.size(),
                        input.sort()
                )
        );

        return new GetGamesOutput(games);
    }
}
