package com.letraaletra.api.domain.user;

import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.user.exceptions.UserAlreadyInGameException;

public class User {
    private final String id;
    private final String nickname;
    private final String avatar;
    private final String email;
    private final String hashPassword;
    private final String googleId;
    private String currentGameId;

    public User(String id, String nickname, String avatar, String email, String hashPassword, String googleId) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.email = email;
        this.hashPassword = hashPassword;
        this.googleId = googleId;
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

    public String getHashPassword() {
        return hashPassword;
    }

    public String getGoogleId() {
        return googleId;
    }

    public boolean isInGame() {
        return currentGameId != null;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void enterGame(String gameId) {
        if (gameId == null || gameId.isBlank()) {
            throw new GameNotFoundException();
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
