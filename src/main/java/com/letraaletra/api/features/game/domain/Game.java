package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.exception.GameIsRunningException;
import com.letraaletra.api.features.game.domain.participants.Participants;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.game.domain.factory.GameStateFactory;
import com.letraaletra.api.features.game.domain.state.GameState;

import java.util.*;

public class Game {
    private final UUID id;
    private final String code;
    private final String roomName;
    private final Participants participants = new Participants();
    private final RoomSettings roomSettings;
    private final GameType gameType;
    private final UUID createdById;
    private UUID hostId;
    private GameStatus gameStatus;
    private GameState gameState;

    public Game(
            UUID id,
            String code,
            String roomName,
            RoomSettings roomSettings,
            Participant host,
            GameType gameType
    ) {
        this.id = id;
        this.code = code;
        this.roomName = roomName;
        host.changeRole(ParticipantRole.PLAYER);
        participants.join(host, roomSettings);
        this.gameType = gameType;
        this.createdById = host.getUserId();
        this.hostId = host.getUserId();
        this.gameStatus = GameStatus.WAITING;
        this.roomSettings = roomSettings;
    }

    public static Game create(
            UUID id,
            String code,
            String roomName,
            RoomSettings roomSettings,
            Participant host,
            GameType gameType
    ) {
        return new Game(
                id,
                code,
                roomName,
                roomSettings,
                host,
                gameType
        );
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getRoomName() {
        return roomName;
    }

    public GameType getGameType() {
        return gameType;
    }

    public Participants getParticipants() {
        return participants;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public UUID getHostId() {
        return hostId;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public GameState getGameState() {
        return gameState;
    }

    public RoomSettings getRoomSettings() {
        return roomSettings;
    }

    public void start(Board board, GameStateFactory stateGenerator) {
        GameState state = stateGenerator.generate(participants.getParticipants(), board);

        this.gameStatus = GameStatus.RUNNING;
        this.gameState = state;
    }

    public void changePosition(UUID userId, int position) {
        if (gameStatus.equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }

        participants.changePosition(userId, position);
    }

    public void remove(UUID userId) {
        Participant participant = participants.remove(userId);

        if (gameStatus.equals(GameStatus.RUNNING)) {
            gameState.removePlayer(userId);
        }

        if (participants.getParticipants().isEmpty()) {
            return;
        }

        if (participant.getUserId().equals(hostId)) {
            hostId = participants.findNextParticipant();
        }
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
