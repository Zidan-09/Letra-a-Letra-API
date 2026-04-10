package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.security.TokenService;

import java.util.UUID;

public class DefaultGameGenerator {
    private final TokenService tokenService;

    public DefaultGameGenerator(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public DefaultGameResult generate(Participant player1, Participant player2) {
        RoomSettings settings = new RoomSettings(true, true);
        String gameId = UUID.randomUUID().toString();

        Game game = new Game(gameId, "default-code", "default-name", settings, player1);

        String token = tokenService.generateToken(gameId);

        game.join(player2);

        return builtResult(token, game);
    }

    private DefaultGameResult builtResult(String token, Game game) {
        return new DefaultGameResult(token, game);
    }
}
