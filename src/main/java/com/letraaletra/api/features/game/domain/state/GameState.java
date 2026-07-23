package com.letraaletra.api.features.game.domain.state;

import com.letraaletra.api.features.game.domain.GameOverReasons;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.exception.PlayerNotInGameException;
import com.letraaletra.api.features.game.domain.service.GameOver;

import java.time.Instant;
import java.util.*;

public class GameState {
    private final UUID matchId;
    private final Map<UUID, Player> players;
    private final Board board;
    private final List<UUID> turnOrder;
    private int currentTurnIndex;
    private Instant turnEndsAt;
    private int version;

    public GameState(UUID matchId, Map<UUID, Player> players, Board board, Instant turnEnds) {
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

    public UUID getMatchId() {
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

    public void removePlayer(UUID playerId) {
        players.remove(playerId);
    }

    public Optional<GameOver> gameOverBecausePlayerLeft(UUID whoLeft) {
        Player winner = players.values().stream()
                .filter(player -> !player.getUserId().equals(whoLeft))
                .findFirst()
                .orElseThrow(UserNotInGameException::new);

        Player loser = Optional.ofNullable(players.get(whoLeft))
                .orElseThrow(UserNotInGameException::new);

        return Optional.of(new GameOver(
                GameOverReasons.PLAYER_LEFT,
                winner,
                loser
        ));
    }

    public Optional<GameOver> gameOverBecauseScore() {
        List<Player> playersList = players.values().stream().toList();

        Player p1 = playersList.getFirst();
        Player p2 = playersList.get(1);

        if (p1 == null || p2 == null) {
            throw new UserNotInGameException();
        }

        if (p1.getScore() >= 3) {
            return Optional.of(new GameOver(
                    GameOverReasons.SCORE,
                    p1,
                    p2
            ));
        }

        if (p2.getScore() >= 3) {
            return Optional.of(new GameOver(
                    GameOverReasons.SCORE,
                    p2,
                    p1
            ));
        }

        return Optional.empty();
    }

    public Optional<GameOver> gameOverBecauseAfk() {
        List<Player> playersList = players.values().stream().toList();

        Player p1 = playersList.getFirst();
        Player p2 = playersList.get(1);

        if (p1 == null || p2 == null) {
            throw new UserNotInGameException();
        }

        if (p1.getPassedTurn() >= 3) {
            return Optional.of(new GameOver(
                    GameOverReasons.PLAYER_AFK,
                    p2,
                    p1
            ));
        }

        if (p2.getPassedTurn() >= 3) {
            return Optional.of(new GameOver(
                    GameOverReasons.PLAYER_AFK,
                    p1,
                    p2
            ));
        }

        return Optional.empty();
    }

    public Optional<GameOver> gameOverBecauseDisconnection(UUID whoDisconnect) {
        Player winner = players.values().stream()
                .filter(player -> !player.getUserId().equals(whoDisconnect))
                .findFirst()
                .orElseThrow(UserNotInGameException::new);

        Player loser = Optional.ofNullable(players.get(whoDisconnect))
                .orElseThrow(UserNotInGameException::new);

        return Optional.of(new GameOver(
                GameOverReasons.PLAYER_DISCONNECTED,
                winner,
                loser
        ));
    }
}
