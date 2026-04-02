package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.FindByCodeCommand;
import com.letraaletra.api.application.output.game.FindByCodeOutput;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;

public class FindByCodeUseCase {
    private final GameRepository gameRepository;
    private final TokenService tokenService;

    public FindByCodeUseCase(GameRepository gameRepository, TokenService tokenService) {
        this.gameRepository = gameRepository;
        this.tokenService = tokenService;
    }

    public FindByCodeOutput execute(FindByCodeCommand command) {
        Game game = gameRepository.findByCode(command.code());

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
