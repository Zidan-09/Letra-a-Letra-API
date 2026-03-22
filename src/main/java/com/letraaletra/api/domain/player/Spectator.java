package com.letraaletra.api.domain.player;

public class Spectator {
    private final String spectatorId;
    private final String nickname;
    private final String avatar;

    public Spectator(String spectatorId, String nickname, String avatar) {
        this.spectatorId = spectatorId;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public String getSpectatorId() {
        return spectatorId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }
}
