package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.FindByTokenCommand;
import com.letraaletra.api.application.output.game.FindByTokenOutput;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindByTokenGameIdUseCase {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameRepository gameRepository;

    public FindByTokenOutput execute(FindByTokenCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);

        return buildReturn(game, command.token());
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private FindByTokenOutput buildReturn(Game game, String token) {
        return new FindByTokenOutput(
                token,
                game
        );
    }
}
