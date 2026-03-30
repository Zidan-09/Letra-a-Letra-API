package com.letraaletra.api.domain.user;

import com.letraaletra.api.domain.user.exceptions.InvalidIdException;
import com.letraaletra.api.domain.user.exceptions.UserAlreadyInGameException;

public class User {
    private final String id;
    private final String nickname;
    private final String avatar;
    private final String email;
    private final String password;
    private String currentGameId;

    public User(String id, String nickname, String avatar, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isInGame() {
        return currentGameId != null;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void enterGame(String gameId) {
        if (gameId == null || gameId.isBlank()) {
            throw new InvalidIdException();
        }

        if (this.currentGameId != null) {
            throw new UserAlreadyInGameException();
        }

        this.currentGameId = gameId;
    }

    public void leaveGame() {
        this.currentGameId = null;
    }
}
