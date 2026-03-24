package com.letraaletra.api.domain.game;

import com.letraaletra.api.domain.theme.Theme;

public class GameSettings {
    private String hostId;
    private Theme theme;
    private GameMode gameMode;
    private boolean allowSpectators;
    private boolean privateGame;

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

    public void changeTheme(Theme theme) {
        this.theme = theme;
    }

    public void changeGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void toggleAllowSpectators() {
        this.allowSpectators = !this.allowSpectators;
    }

    public void togglePrivateGame() {
        this.privateGame = !this.privateGame;
    }
}
