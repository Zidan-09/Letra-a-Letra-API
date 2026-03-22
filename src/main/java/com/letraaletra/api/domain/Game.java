package com.letraaletra.api.domain;

import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.participant.Participant;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final String id;
    private final String roomName;
    private final List<Participant> participants = new ArrayList<>(7);
    private GameStatus gameStatus;
    private GameState gameState;
    private GameSettings gameSettings;

    public Game(String id, String roomName, GameSettings gameSettings, Participant host) {
        this.id = id;
        this.roomName = roomName;
        this.participants.add(host);
        this.gameStatus = GameStatus.WAITING;
        this.gameSettings = gameSettings;
    }

    public String getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<Participant> getParticipants() {
        return List.copyOf(participants);
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public void participantJoinGame(Participant participant) {
        participants.add(participant);
    }

    public void participantLeaveGame(String userId) {
        participants.removeIf(p -> p.getUserId().equals(userId));
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void updateGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
}
