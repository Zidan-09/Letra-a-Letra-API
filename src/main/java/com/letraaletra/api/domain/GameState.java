package com.letraaletra.api.domain;

import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GameState {
    private final Map<String, Player> players;
    private final Board board;
    private final List<String> turnOrder;
    private int currentTurnIndex;

    public GameState(Map<String, Player> players, Board board) {
        this.players = players;
        this.board = board;
        this.turnOrder = players.values().stream()
                .sorted(Comparator.comparingInt(Player::getTurn))
                .map(Player::getPlayerId)
                .toList();

        this.currentTurnIndex = 0;
    }

    public Map<String, Player> getPlayers() {
        return Map.copyOf(players);
    }

    public Board getBoard() {
        return board;
    }

    public void nextTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
    }
}
