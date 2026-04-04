package com.letraaletra.api.domain.game;

import com.letraaletra.api.domain.game.exception.RoomFullException;
import com.letraaletra.api.domain.game.participant.exception.InvalidRoomPositionException;
import com.letraaletra.api.domain.game.participant.exception.ParticipantAlreadyBannedException;
import com.letraaletra.api.domain.game.participant.exception.ParticipantNotBannedException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.exception.UserNotInGameException;
import com.letraaletra.api.domain.user.exceptions.UserAlreadyInGameException;

import java.util.*;

public class Game {
    private final String id;
    private final String code;
    private final String roomName;
    private final Map<String, Participant> participants = new HashMap<>();
    private final Map<Integer, String> positions = new HashMap<>();
    private final RoomSettings roomSettings;
    private final Set<String> blacklist = new HashSet<>();
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

    public synchronized void join(Participant participant) {
        boolean alreadyExists = participants.values().stream()
                .anyMatch(p -> p.getUserId().equals(participant.getUserId()));

        if (alreadyExists) {
            throw new UserAlreadyInGameException();
        }

        int size = participants.size();

        boolean isFullWithoutSpectators = !roomSettings.roomAllowSpectators() && size >= 2;
        boolean isFullWithSpectators = size >= 7;

        if (isFullWithoutSpectators || isFullWithSpectators) {
            throw new RoomFullException();
        }

        positions.put(participants.size(), participant.getUserId());
        participants.put(participant.getUserId(), participant);
    }

    public synchronized void remove(String userId) {
        Participant participant = participants.get(userId);

        if (participant == null) {
            throw new UserNotInGameException();
        }

        participants.remove(userId);
        positions.entrySet().removeIf(entry -> entry.getValue().equals(userId));

        if (participants.isEmpty()) {
            return;
        }

        if (participant.getUserId().equals(hostId)) {
            hostId = participants.keySet().iterator().next();
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
        Participant participant = participants.get(userId);

        if (participant == null) {
            throw new UserNotInGameException();
        }

        if (positions.get(position) != null) {
            throw new InvalidRoomPositionException();
        }

        participant.changeRole(position > 2 ? ParticipantRole.SPECTATOR : ParticipantRole.PLAYER);

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

    public boolean isBlackListed(String userId) {
        return blacklist.contains(userId);
    }

    public void addToBlackList(String userId) {
        if (blacklist.contains(userId)) {
            throw new ParticipantAlreadyBannedException();
        }

        remove(userId);
        blacklist.add(userId);
    }

    public void removeFromBlackList(String userId) {
        if (!blacklist.contains(userId)) {
            throw new ParticipantNotBannedException();
        }

        blacklist.remove(userId);
    }

    public int getAmountPlayers() {
        return participants.values().stream()
                .filter(p -> p.getRole() == ParticipantRole.PLAYER)
                .toList().size();
    }
}
