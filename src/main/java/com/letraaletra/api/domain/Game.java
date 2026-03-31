package com.letraaletra.api.domain;

import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.domain.game.exceptions.InvalidRoomPositionException;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.user.exceptions.UserAlreadyInGameException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final String id;
    private final String code;
    private final String roomName;
    private final Map<String, Participant> participants = new HashMap<>();
    private final RoomSettings roomSettings;
    private final Map<Integer, String> positions = new HashMap<>();
    private String hostId;
    private GameStatus gameStatus;
    private GameState gameState;

    public Game(String id, String code, String roomName, RoomSettings roomSettings, Participant host) {
        this.id = id;
        this.code = code;
        this.roomName = roomName;
        this.participants.put(host.getUserId(), host);
        this.hostId = host.getUserId();
        this.positions.put(0, host.getUserId());
        this.gameStatus = GameStatus.WAITING;
        this.roomSettings = roomSettings;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<Participant> getParticipants() {
        return List.copyOf(participants.values());
    }

    public String getHostId() {
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

    public Participant getParticipant(String sessionId) {
        return participants.values().stream()
                .filter(p -> p.getSocketId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    public Participant getParticipantByUserId(String userId) {
        return participants.get(userId);
    }

    public Map<Integer, String> getPositions() {
        return positions;
    }

    public void join(Participant participant) {
        boolean alreadyExists = participants.values().stream()
                .anyMatch(p -> p.getUserId().equals(participant.getUserId()));

        if (alreadyExists) {
            throw new UserAlreadyInGameException();
        }

        participants.put(participant.getUserId(), participant);
        positions.put(participants.size(), participant.getUserId());
    }

    public void remove(String userId) {
        Participant participant = participants.remove(userId);
        if (participant == null) {
            throw new UserNotInGameException();
        }

        if (participants.isEmpty()) {
            return;
        }

        if (participant.getUserId().equals(hostId)) {
            participants.values().stream()
                    .findFirst()
                    .ifPresent(p -> hostId = p.getUserId());
        }
    }

    public void reconnect(String userId, String sessionId) {
        Participant participant = participants.get(userId);

        if (participant == null) {
            throw new UserNotInGameException();
        }

        participant.connect(sessionId);
    }

    public void changePosition(String userId, int position) {
        if (positions.get(position) != null) {
            throw new InvalidRoomPositionException();
        }

        positions.entrySet().removeIf(entry -> entry.getValue().equals(userId));

        positions.put(position, userId);
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public ParticipantRole nextParticipantRole() {
        long players = participants.values().stream()
                .filter(p -> p.getRole() == ParticipantRole.PLAYER)
                .limit(3)
                .count();

        return players >= 2
                ? ParticipantRole.SPECTATOR
                : ParticipantRole.PLAYER;
    }

    public Participant findBySession(String sessionId) {
        return participants.values().stream()
                .filter(p -> p.getSocketId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }
}
