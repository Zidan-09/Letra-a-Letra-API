package com.letraaletra.api.features.participant.domain;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.List;

public class Participant {
    private final String userId;
    private final String nickname;
    private final List<InventoryItem> cosmeticsEquipped;
    private String socketId;
    private ParticipantRole role;
    private boolean connected;

    public Participant(String userId, String socketId, String nickname, List<InventoryItem> cosmeticsEquipped) {
        this.userId = userId;
        this.socketId = socketId;
        this.nickname = nickname;
        this.cosmeticsEquipped = cosmeticsEquipped;
        this.connected = true;
        this.role = ParticipantRole.SPECTATOR;
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

    public List<InventoryItem> getCosmeticsEquipped() {
        return cosmeticsEquipped;
    }

    public ParticipantRole getRole() {
        return role;
    }

    public void changeRole(ParticipantRole role) {
        this.role = role;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect(String sessionId) {
        this.connected = true;
        this.socketId = sessionId;
    }

    public void disconnect() {
        this.connected = false;
    }
}
