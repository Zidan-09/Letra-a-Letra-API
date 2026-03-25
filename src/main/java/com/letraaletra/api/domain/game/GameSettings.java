package com.letraaletra.api.domain.game;

import com.letraaletra.api.domain.theme.Theme;

public class GameSettings {
    private String hostId;
    private final Theme theme;
    private final GameMode gameMode;
    private final boolean allowSpectators;
    private final boolean privateGame;

    public GameSettings(String hostId, Theme theme, GameMode gameMode, boolean allowSpectators, boolean privateGame) {
        this.hostId = hostId;
        this.theme = theme;
        this.gameMode = gameMode;
        this.allowSpectators = allowSpectators;
        this.privateGame = privateGame;
    }

    public String getHostId() {
        return hostId;
    }

    public Theme getTheme() {
        return theme;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public boolean isAllowSpectators() {
        return allowSpectators;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }

    public void changeHost(String newHostId) {
        this.hostId = newHostId;
    }
}
