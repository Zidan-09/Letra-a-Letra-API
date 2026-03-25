package com.letraaletra.api.domain.participant;

public class Participant {
    private final String userId;
    private final String socketId;
    private final String nickname;
    private final String avatar;
    private ParticipantRole role;

    public Participant(String userId, String socketId, String nickname, String avatar, ParticipantRole role) {
        this.userId = userId;
        this.socketId = socketId;
        this.nickname = nickname;
        this.avatar = avatar;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getSocketId() {
        return socketId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public ParticipantRole getRole() {
        return role;
    }

    public void changeRole(ParticipantRole role) {
        this.role = role;
    }
}
