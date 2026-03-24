package com.letraaletra.api.domain.player;

import com.letraaletra.api.dto.response.player.PlayerDTO;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String userId;
    private final String nickname;
    private final String avatar;
    private final int turn;
    private int score = 0;

    public Player(String userId, String nickname, String avatar, int turn) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
        this.turn = turn;
    }

    public String getUserId() {
        return userId;
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
                userId,
                nickname,
                avatar,
                score,
                0
        );
    }
}
