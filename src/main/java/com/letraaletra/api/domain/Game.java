package com.letraaletra.api.domain;

import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.dto.response.game.GameDTO;
import com.letraaletra.api.dto.response.game.GameOverDTO;
import com.letraaletra.api.dto.response.game.GameStateDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final String id;
    private final String roomName;
    private final Map<String, Participant> participants = new HashMap<>();
    private GameStatus gameStatus;
    private GameState gameState;
    private GameSettings gameSettings;

    public Game(String id, String roomName, GameSettings gameSettings, Participant host) {
        this.id = id;
        this.roomName = roomName;
        this.participants.put(host.getUserId(), host);
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
        return List.copyOf(participants.values());
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
        participants.put(participant.getUserId(), participant);
    }

    public void participantLeaveGame(String userId) {
        participants.remove(userId);
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

    public GameStateDTO getGameStateToSend() {
        return new GameStateDTO(
                gameState.getPlayersDTO(),
                gameState.getBoard().toView(),
                gameState.currentPlayerTurn()
        );
    }

    public GameDTO getGameToSend() {
        return new GameDTO(
                id,
                roomName,
                participants.values().stream().map(Participant::getParticipantToSend).toList()
        );
    }

    public GameOverDTO getGameOverToSend() {
        return new GameOverDTO(
                gameState.getPlayersDTO()
        );
    }
}
