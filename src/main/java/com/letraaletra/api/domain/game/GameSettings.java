package com.letraaletra.api.domain.game;

public class GameSettings {
    private String hostId;
    private final String themeId;
    private final GameMode gameMode;
    private final boolean allowSpectators;
    private final boolean privateGame;

    public GameSettings(String hostId, String themeId, GameMode gameMode, boolean allowSpectators, boolean privateGame) {
        this.hostId = hostId;
        this.themeId = themeId;
        this.gameMode = gameMode;
        this.allowSpectators = allowSpectators;
        this.privateGame = privateGame;
    }

    public String getHostId() {
        return hostId;
    }

    public String getThemeId() {
        return themeId;
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
