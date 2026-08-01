package com.letraaletra.api.features.game.domain.factory;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.RoomSettings;

public class GameFactory {
    public static Game match(String code) {
        RoomSettings settings = new RoomSettings(true, true);

        return Game.create(code, "default-match", settings, GameType.MATCHMAKING);
    }

    public static Game rank(String code) {
        RoomSettings settings = new RoomSettings(true, true);

        return Game.create(code, "default-ranking", settings, GameType.RANKING);
    }

    public static Game custom(String code, RoomSettings roomSettings, String roomName) {
        return Game.create(code, roomName, roomSettings, GameType.CUSTOM);
    }
}
