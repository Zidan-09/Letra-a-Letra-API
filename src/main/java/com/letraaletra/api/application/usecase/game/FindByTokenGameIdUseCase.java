package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.FindByTokenCommand;
import com.letraaletra.api.application.output.game.FindByTokenOutput;
import com.letraaletra.api.infrastructure.security.JsonWebTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindByTokenGameIdUseCase {
    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Autowired
    private GameRepository gameRepository;

    public FindByTokenOutput execute(FindByTokenCommand command) {
        String gameId = jsonWebTokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);

        return buildReturn(game);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private FindByTokenOutput buildReturn(Game game) {
        return new FindByTokenOutput(
                game
        );
    }
}
