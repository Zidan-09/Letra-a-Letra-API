package com.letraaletra.api.features.game.domain.factory;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.RoomSettings;
import com.letraaletra.api.features.participant.domain.Participant;

import java.util.UUID;

public class DefaultGameFactory {
    public DefaultGameResult match(Participant player1, Participant player2, String code) {
        RoomSettings settings = new RoomSettings(true, true);
        UUID gameId = UUID.randomUUID();

        Game game = new Game(gameId, code, "default-name", settings, player1, GameType.MATCHMAKING);

        game.join(player2);

        return builtResult(game);
    }

    public DefaultGameResult rank(Participant player1, Participant player2, String code) {
        RoomSettings settings = new RoomSettings(true, true);
        UUID gameId = UUID.randomUUID();

        Game game = new Game(gameId, code, "default-name", settings, player1, GameType.RANKING);

        game.join(player2);

        return builtResult(game);
    }

    private DefaultGameResult builtResult(Game game) {
        return new DefaultGameResult(game);
    }
}
