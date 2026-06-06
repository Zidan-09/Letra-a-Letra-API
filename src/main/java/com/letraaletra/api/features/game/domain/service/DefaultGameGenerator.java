package com.letraaletra.api.features.game.domain.service;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.RoomSettings;
import com.letraaletra.api.features.participant.domain.Participant;

import java.util.UUID;

public class DefaultGameGenerator {
    public DefaultGameResult generate(Participant player1, Participant player2, String code) {
        RoomSettings settings = new RoomSettings(true, true);
        String gameId = UUID.randomUUID().toString();

        Game game = new Game(gameId, code, "default-name", settings, player1, GameType.MATCHMAKING);

        game.join(player2);

        return builtResult(game);
    }

    private DefaultGameResult builtResult(Game game) {
        return new DefaultGameResult(game);
    }
}
