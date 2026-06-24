package com.letraaletra.api.features.game.domain.state;

import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.exception.PlayerNotInGameException;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

import java.time.Instant;
import java.util.*;

public class GameState {
    private final String matchId;
    private final Map<UUID, Player> players;
    private final Board board;
    private final List<UUID> turnOrder;
    private int currentTurnIndex;
    private Instant turnEndsAt;
    private int version;

    public GameState(String matchId, Map<UUID, Player> players, Board board, Instant turnEnds) {
        this.matchId = matchId;
        this.players = players;
        this.board = board;

        List<Player> list = new ArrayList<>(players.values());
        Collections.shuffle(list);

        this.turnOrder = list.stream()
                .map(Player::getUserId)
                .toList();

        this.currentTurnIndex = 0;
        this.turnEndsAt = turnEnds;
        this.version = 1;
    }

    public String getMatchId() {
        return matchId;
    }

    public Map<UUID, Player> getPlayers() {
        return Map.copyOf(players);
    }

    public Player getPlayerOrThrow(UUID userId) {
        Player player = players.get(userId);

        if (player == null) {
            throw new PlayerNotInGameException();
        }

        return player;
    }

    public Board getBoard() {
        return board;
    }

    public Instant getCurrentTurnEnds() {
        return turnEndsAt;
    }

    public int getVersion() {
        return version;
    }

    public void setTurnEndsAt(Instant turnEndsAt) {
        this.turnEndsAt = turnEndsAt;
    }

    public UUID currentPlayerTurn() {
        return turnOrder.get(currentTurnIndex);
    }

    public void nextTurn(Instant turnEnds) {
        version++;
        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
        turnEndsAt = turnEnds;
        players.values().forEach(Player::decrementEffectDuration);
    }

    public boolean isTurnExpired(Instant now) {
        return now.isAfter(turnEndsAt);
    }

    public GameOverResult gameOverChecker() {
        List<Player> playersList = players.values().stream().toList();

        if (players.isEmpty()) {
            return new GameOverResult(true, null, null);
        }

        if (players.size() == 1) {
            Player winner = playersList.getFirst();
            return new GameOverResult(true, winner, null);
        }

        Player p1 = playersList.getFirst();
        Player p2 = playersList.get(1);

        if (p1.getScore() == 3) {
            return new GameOverResult(true, p1, p2);
        }

        if (p2.getScore() == 3) {
            return new GameOverResult(true, p2, p1);
        }

        return new GameOverResult(false, null, null);
    }

    public void removePlayer(UUID playerId) {
        players.remove(playerId);
    }
}
