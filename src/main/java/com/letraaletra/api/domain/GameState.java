package com.letraaletra.api.domain;

import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.dto.response.player.PlayerDTO;

import java.util.*;

public class GameState {
    private final Map<String, Player> players;
    private final Board board;
    private final List<String> turnOrder;
    private int currentTurnIndex;

    public GameState(Map<String, Player> players, Board board) {
        this.players = players;
        this.board = board;

        List<Player> list = new ArrayList<>(players.values());
        Collections.shuffle(list);

        this.turnOrder = list.stream()
                .map(Player::getUserId)
                .toList();

        this.currentTurnIndex = 0;
    }

    public Map<String, Player> getPlayers() {
        return Map.copyOf(players);
    }

    public Board getBoard() {
        return board;
    }

    public String currentPlayerTurn() {
        return turnOrder.get(currentTurnIndex);
    }

    public void nextTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
    }
}
