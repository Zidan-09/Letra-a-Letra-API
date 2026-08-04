package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.exception.GameIsRunningException;
import com.letraaletra.api.features.game.domain.exception.InsufficientPlayersException;
import com.letraaletra.api.features.game.domain.participants.Participants;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.game.domain.factory.GameStateFactory;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.participant.domain.exception.InvalidModerateActionException;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanModerateException;
import com.letraaletra.api.features.user.domain.User;

import java.util.*;

public class Game {
    private final UUID id;
    private final String code;
    private final String roomName;
    private final Participants participants = new Participants();
    private final RoomSettings roomSettings;
    private final GameType gameType;
    private UUID createdById;
    private UUID hostId;
    private GameStatus gameStatus;
    private GameState gameState;

    private Game(
            UUID id,
            String code,
            String roomName,
            RoomSettings roomSettings,
            GameType gameType
    ) {
        this.id = id;
        this.code = code;
        this.roomName = roomName;
        this.gameType = gameType;
        this.gameStatus = GameStatus.WAITING;
        this.roomSettings = roomSettings;
    }

    public static Game create(
            String code,
            String roomName,
            RoomSettings roomSettings,
            GameType gameType
    ) {
        return new Game(
                UUID.randomUUID(),
                code,
                roomName,
                roomSettings,
                gameType
        );
    }

    public static Game restore(
            UUID id,
            String code,
            String roomName,
            RoomSettings roomSettings,
            GameType gameType
    ) {
        return new Game(
                id,
                code,
                roomName,
                roomSettings,
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

    public void join(User user, String session) {
        Participant participant = Participant.create(user, session);

        if (participants.getParticipants().isEmpty()) {
            createdById = participant.getUserId();
            hostId = participant.getUserId();
        }

        participants.join(participant, roomSettings);
    }

    public void start(Board board) {
        if (gameStatus.equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }

        if (participants.getAmountPlayers() < 2) {
            throw new InsufficientPlayersException();
        }

        GameState state = GameStateFactory.generate(participants.getParticipants(), board);

        this.gameStatus = GameStatus.RUNNING;
        this.gameState = state;
    }

    public void changePosition(UUID userId, int position) {
        if (gameStatus.equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }

        participants.changePosition(userId, position, roomSettings);
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

    public void banParticipant(UUID hostId, UUID targetId) {
        validateHostAction(hostId, targetId);

        participants.addToBlackList(targetId);
        remove(targetId);
    }

    public void removeBan(UUID hostId, UUID targetId) {
        validateHostAction(hostId, targetId);

        participants.removeFromBlackList(targetId);

    }

    public void kickParticipant(UUID hostId, UUID targetId) {
        validateHostAction(hostId, targetId);

        remove(targetId);
    }

    private void validateHostAction(UUID hostId, UUID targetId) {
        if (!this.hostId.equals(hostId)) {
            throw new OnlyHostCanModerateException();
        }

        if (hostId.equals(targetId)) {
            throw new InvalidModerateActionException();
        }
    }
}
