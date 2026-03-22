package com.letraaletra.api.domain.player;

import com.letraaletra.api.dto.response.player.PlayerDTO;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String playerId;
    private final String socketId;
    private final int turn;
    private int score = 0;

    public Player(String playerId, String socketId, int turn) {
        this.playerId = playerId;
        this.socketId = socketId;
        this.turn = turn;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getSocketId() {
        return socketId;
    }

    public int getTurn() {
        return turn;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }

    public PlayerDTO getPlayerToSend() {
        return new PlayerDTO(
                playerId,
                score
        );
    }
}
