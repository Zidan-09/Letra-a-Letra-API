package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.player.Player;

import java.util.List;

public class GameOverChecker {
    public GameOverResult evaluate(GameState state) {
        List<Player> players = state.getPlayers().values().stream().toList();

        if (players.size() == 1) {
            Player winner = players.getFirst();
            return new GameOverResult(true, winner, null);
        }

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        if (p1.getScore() == 3) {
            return new GameOverResult(true, p1, p2);
        }

        if (p2.getScore() == 3) {
            return new GameOverResult(true, p2, p1);
        }

        return new GameOverResult(false, null, null);
    }
}