package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.FindByCodeCommand;
import com.letraaletra.api.application.output.game.FindByCodeOutput;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindByCodeUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService jsonWebTokenService;

    public FindByCodeOutput execute(FindByCodeCommand command) {
        Game game = gameRepository.findByCode(command.code());

        validateGame(game);

        String token = jsonWebTokenService.generateToken(game.getId());

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
