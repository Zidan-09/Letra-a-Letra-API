package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindByTokenGameIdUseCase {
    @Autowired
    private GlobalTokenService globalTokenService;

    @Autowired
    private GameRepository gameRepository;

    public Game execute(String tokenGameId) {
        String gameId = globalTokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);

        return game;
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }
}
