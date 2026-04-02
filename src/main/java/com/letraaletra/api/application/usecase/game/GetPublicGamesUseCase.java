package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.output.game.GetGamesOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetPublicGamesUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService tokenService;

    public GetGamesOutput execute() {
        List<Game> games = gameRepository.getPublic();

        Map<String, String> tokens = new HashMap<>();

        for (Game game : games) {
            String token = tokenService.generateToken(game.getId());

            tokens.put(token, game.getId());
        }

        return buildReturn(games, tokens);
    }

    private GetGamesOutput buildReturn(List<Game> games, Map<String, String> tokens) {
        return new GetGamesOutput(
                games,
                tokens
        );
    }
}
