package com.letraaletra.api.domain.game;

import com.letraaletra.api.domain.theme.Theme;

public class GameSettings {
    private String hostId;
    private Theme theme;
    private GameMode gameMode;
    private boolean allowSpectators;
    private boolean privateRoom;

    public GameSettings(String hostId, Theme theme, GameMode gameMode, boolean allowSpectators, boolean privateRoom) {
        this.hostId = hostId;
        this.theme = theme;
        this.gameMode = gameMode;
        this.allowSpectators = allowSpectators;
        this.privateRoom = privateRoom;
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

    public boolean isPrivateRoom() {
        return privateRoom;
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

    public void togglePrivateRoom() {
        this.privateRoom = !this.privateRoom;
    }
}
