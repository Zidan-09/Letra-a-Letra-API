package com.letraaletra.api.features.user.domain;

public class UserFactory {
    public static User createLocal(String nickname, String email, String passwordHash) {
        return User.create(
                nickname,
                email,
                passwordHash,
                null,
                true
        );
    }

    public static User createGoogle(String nickname, String email, String googleId) {
        return User.create(
                nickname,
                email,
                null,
                googleId,
                true
        );
    }
}