package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindActiveGameByRoomNameInput;
import com.letraaletra.api.features.game.application.output.FindActiveGameByRoomNameOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class FindActiveGameByRoomNameUseCase implements UseCase<FindActiveGameByRoomNameInput, FindActiveGameByRoomNameOutput> {
    private final GameQueryService gameQueryService;

    public FindActiveGameByRoomNameUseCase(GameQueryService gameQueryService) {
        this.gameQueryService = gameQueryService;
    }

    @Override
    public FindActiveGameByRoomNameOutput execute(FindActiveGameByRoomNameInput input) {
        Page<Game> games = gameQueryService.searchActiveByRoomName(input);

        return new FindActiveGameByRoomNameOutput(games);
    }
}
